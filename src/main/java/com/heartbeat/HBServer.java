package com.heartbeat;

import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.ReactiveCluster;
import com.diabolicallabs.vertx.cron.CronObservable;
import com.heartbeat.common.Constant;
import com.heartbeat.controller.*;
import com.heartbeat.db.Cruder;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserFight;
import com.heartbeat.model.data.UserInbox;
import com.heartbeat.model.data.UserLDB;
import com.statics.*;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.RxHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static com.heartbeat.common.Constant.*;

/*
 * RXServer = reactive server, a server that try not to waiting on anything, hope so!!
 */

//  second, minute, hour, day of month, month, day(s) of week
//  * "0 0 * * * *"           = the top of every hour of every day.
//  * "*/10 * * * * *"        = every ten seconds.
//  * "0 0 8-10 * * *"        = 8, 9 and 10 o'clock of every day.
//  * "0 0 8,10 * * *"        = 8 and 10 o'clock of every day.
//  * "0 0/30 8-10 * * *"     = 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day.
//  * "0 0 9-17 * * MON-FRI"  = on the hour nine-to-five weekdays
//  * "0 0 0 25 12 ?"         = every Christmas Day at midnight
//  (*) means match any
//  */X means "every X"
//  ? ("no specific value")


// AVAILABILITY > CONSISTENCY

public class HBServer extends AbstractVerticle {
  private static final Logger     LOGGER = LoggerFactory.getLogger(HBServer.class);

  public  static Cruder<Session>  cruder;
  public  static ReactiveCluster  rxCluster;
  public  static ReactiveBucket   rxSessionBucket;
  public  static ReactiveBucket   rxIndexBucket;
  public  static ReactiveBucket   rxPersistBucket;

  public  static JsonObject       systemConfig;
  public  static JsonObject       localConfig;

  public  static Disposable        gsOpenTask;
  public  static Disposable        gsCloseTask;
  public  static Disposable        newDayTask;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    startCronTask(vertx);
  }

  @Override
  public void start(Promise<Void> startPromise) {
    try {
      loadStaticData();

      SessionPool.checkHeartBeat.run();
      GroupPool.groupSyncTask.run();

      UserFight.serverStartup();
      Constant.serverStartUp();

      UserLDB.loadLDBFromDB(LEADER_BOARD.TALENT_LDB_ID);
      UserLDB.loadLDBFromDB(LEADER_BOARD.FIGHT_LDB_ID);
      UserInbox.loadInboxFromDB();
    }
    catch (Exception ioe) {
      LOGGER.error(ioe.getMessage());
      startPromise.fail(ioe);
    }

    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(ar -> {
      if (ar.succeeded()) {
        systemConfig = ar.result();
        Router router = Router.router(vertx);
        AuthController authController = new AuthController(vertx);

        router.route().handler(CorsHandler.create(".*.")
                .allowCredentials(true)
                .allowedHeader("Access-Control-Allow-Method")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Content-Type")
                .allowedMethod(HttpMethod.POST));

        router.route().handler(BodyHandler.create());
        router.route("/api/*").handler(JWTAuthHandler.create(authController.getJWTProvider(), "/api/auth"));
        router.post("/api/auth").handler(authController);
        router.post("/api/system").handler(new SystemController());
        router.post("/api/profile").handler(new ProfileController());
        router.post("/api/media").handler(new MediaController());
        router.post("/api/production").handler(new ProductionController());
        router.post("/api/idol").handler(new IdolController());
        router.post("/api/fight").handler(new FightController());
        router.post("/api/item").handler(new ItemController());
        router.post("/api/travel").handler(new TravelController());
        router.post("/api/title").handler(new TitleController());
        router.post("/api/group").handler(new GroupController());
        router.post("/api/daily_mission").handler(new DailyMissionController());
        router.post("/api/achievement").handler(new AchievementController());
        router.post("/api/mission").handler(new MissionController());
        router.post("/api/rollcall").handler(new RollCallController());
        router.post("/api/event").handler(new EventController());
        router.post("/api/ranking").handler(new RankingController());
        router.post("/api/leaderboard").handler(new LeaderBoardController());
        router.post("/api/inbox").handler(new InboxController());

        router.post("/gm/session_inject").handler(new SessionInjectController());
        router.post("/gm/constant").handler(new ConstantInjectController());

        router.get("/loaderio-f8c2671f6ccbeec4f3a09a972475189c/").handler(ctx ->
                ctx.response().end("loaderio-f8c2671f6ccbeec4f3a09a972475189c"));

        HttpServerOptions options = new HttpServerOptions().setSsl(true).setKeyStoreOptions(
                new JksOptions().
                        setPath("keystore.jks").
                        setPassword("changeit")
        );
        vertx.createHttpServer(options)
                .requestHandler(router).listen(localConfig.getInteger("HTTP.PORT", 8080));

        startPromise.complete();
      }
      else {
        startPromise.fail("fail to get config");
      }
    });
  }

  public static void main(String[] args) throws IOException {
    String conf             = new String(Files.readAllBytes(Paths.get("config.json")));
    localConfig             = new JsonObject(conf);
    overrideConstant();

    rxCluster       = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
    rxSessionBucket = HBServer.rxCluster.bucket("sessions");
    rxIndexBucket   = HBServer.rxCluster.bucket("index");
    rxPersistBucket = HBServer.rxCluster.bucket("persist");

    //for logging backend
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");

    //for faster startup, fucking couchbase java sdk T___T
    cruder = CBSession.getInstance();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      SessionPool.removeAll();
      GroupPool.removeAll();
      UserLDB.syncLDBToDB(LEADER_BOARD.TALENT_LDB_ID);
      UserLDB.syncLDBToDB(LEADER_BOARD.FIGHT_LDB_ID);
      LOGGER.info("HBServer shutdown hook");
    }));

    Vertx.vertx().deployVerticle(HBServer.class.getName());
  }

  private static void overrideConstant() {
    DB.HOST                                = localConfig.getString("DB.HOST");
    DB.USER                                = localConfig.getString("DB.USER");
    DB.PWD                                 = localConfig.getString("DB.PWD");
    ONLINE_INFO.ONLINE_HEARTBEAT_TIME      = localConfig.getInteger("ONLINE_INFO.ONLINE_HEARTBEAT_TIME");
    SCHEDULE.TIME_ZONE                     = localConfig.getString("SCHEDULE.TIMEZONE");
  }

  private static void startCronTask(Vertx vertx) {

    Scheduler scheduler = RxHelper.scheduler(vertx);
    newDayTask          = CronObservable.cronspec(scheduler, "0 0 0 * * ? *", Constant.SCHEDULE.TIME_ZONE)
            .subscribe(
                    timed -> {
                      SessionPool.dailyReset.run();
                      LOGGER.info("execute new day task");
                    },
                    fault -> LOGGER.error("error new day task")
            );

    String gameShowOpenCron = String.format("%d %d %d,%d * * ? *",
            Constant.SCHEDULE.gameShowOneOpenSec,
            Constant.SCHEDULE.gameShowOneOpenMin,
            Constant.SCHEDULE.gameShowOneOpenHour,
            Constant.SCHEDULE.gameShowTwoOpenHour);
    gsOpenTask          = CronObservable.cronspec(scheduler, gameShowOpenCron, Constant.SCHEDULE.TIME_ZONE)
            .subscribe(
                    timed -> {
                      Constant.SCHEDULE.gameShowOpen = true;
                      SessionPool.resetGameShowIdols.run();
                      LOGGER.info("open game show");
                    },
                    fault -> LOGGER.error("error open game show task")
            );

    String gameShowCloseCron = String.format("%d %d %d,%d * * ? *",
            Constant.SCHEDULE.gameShowOneCloseSec,
            Constant.SCHEDULE.gameShowOneCloseMin,
            Constant.SCHEDULE.gameShowOneCloseHour,
            Constant.SCHEDULE.gameShowTwoCloseHour);
    gsCloseTask         = CronObservable.cronspec(scheduler, gameShowCloseCron, Constant.SCHEDULE.TIME_ZONE)
            .subscribe(
                    timed -> {
                      Constant.SCHEDULE.gameShowOpen = false;
                      SessionPool.resetGameShowIdols.run();
                      LOGGER.info("close game show");
                    },
                    fault -> LOGGER.error("error close game show task")
            );
  }

  private static void loadStaticData() throws Exception {

    String servantJson = new String(Files.readAllBytes(Paths.get("data/json/servants.json")),
            StandardCharsets.UTF_8);
    ServantData.loadJson(servantJson);

    String officeJson = new String(Files.readAllBytes(Paths.get("data/json/office.json")),
            StandardCharsets.UTF_8);
    OfficeData.loadJson(officeJson);

    String specialityJson = new String(Files.readAllBytes(Paths.get("data/json/specialty.json")),
            StandardCharsets.UTF_8);
    SpecialityData.loadJson(specialityJson);

    String servantLVJson = new String(Files.readAllBytes(Paths.get("data/json/servantLV.json")),
            StandardCharsets.UTF_8);
    ServantLVData.loadJson(servantLVJson);

    String servantHaloJson = new String(Files.readAllBytes(Paths.get("data/json/servantHaloBase.json")),
            StandardCharsets.UTF_8);
    HaloBaseData.loadJson(servantHaloJson);

    String haloJson = new String(Files.readAllBytes(Paths.get("data/json/Halo.json")),
            StandardCharsets.UTF_8);
    HaloData.loadJson(haloJson);

    String propJson = new String(Files.readAllBytes(Paths.get("data/json/props.json")),
            StandardCharsets.UTF_8);
    PropData.loadJson(propJson);

    String mediaJson = new String(Files.readAllBytes(Paths.get("data/json/media.json")),
            StandardCharsets.UTF_8);
    MediaData.loadJson(mediaJson);

    String honorJson = new String(Files.readAllBytes(Paths.get("data/json/servantHonor.json")),
            StandardCharsets.UTF_8);
    ServantHonorData.loadJson(honorJson);

    String headJson = new String(Files.readAllBytes(Paths.get("data/json/head.json")),
            StandardCharsets.UTF_8);
    HeadData.loadJson(headJson);

    String dropJson = new String(Files.readAllBytes(Paths.get("data/json/drop.json")),
            StandardCharsets.UTF_8);
    DropData.loadJson(dropJson);

    String bookLimitJson = new String(Files.readAllBytes(Paths.get("data/json/servantBookLimit.json")),
            StandardCharsets.UTF_8);
    BookLimitData.loadJson(bookLimitJson);

    String fightBossJson     = new String(Files.readAllBytes(Paths.get("data/json/fightBoss.json")),
            StandardCharsets.UTF_8);
    FightBossData.loadJson(fightBossJson);

    String fightJson = new String(Files.readAllBytes(Paths.get("data/json/fight.json")),
            StandardCharsets.UTF_8);
    FightData.loadJson(fightJson);

    String gameShowJson = new String(Files.readAllBytes(Paths.get("data/json/gameshow.json")),
            StandardCharsets.UTF_8);
    GameShowData.loadJson(gameShowJson);

    String runShowJson     = new String(Files.readAllBytes(Paths.get("data/json/runshow.json")),
            StandardCharsets.UTF_8);
    RunShowData.loadJson(runShowJson);

    String shoppingJson    = new String(Files.readAllBytes(Paths.get("data/json/shopping.json")),
            StandardCharsets.UTF_8);
    ShoppingData.loadJson(shoppingJson);

    //todo this is ORACLE java! group, join...JAVA x SQL!
    //1 month later pls don't ever ask me about this chunk of code T___T
    String travelJson    = new String(Files.readAllBytes(Paths.get("data/json/travel.json")),
            StandardCharsets.UTF_8);
    TravelData.loadJson(travelJson, () ->
            TravelData.npcTypeMap = TravelData.travelNPCMap
                    .values()
                    .stream()
                    .collect(Collectors.groupingBy(TravelData.TravelNPC::getType, Collectors.toList())));

    String vipJson    = new String(Files.readAllBytes(Paths.get("data/json/vip.json")),
            StandardCharsets.UTF_8);
    VipData.loadJson(vipJson);

    String companyEventJson = new String(Files.readAllBytes(Paths.get("data/json/companyEvent.json")),
            StandardCharsets.UTF_8);
    GroupMissionData.loadJson(companyEventJson);

    String dailyMission = new String(Files.readAllBytes(Paths.get("data/json/dailyMission.json")),
            StandardCharsets.UTF_8);
    DailyMissionData.loadJson(dailyMission);

    String crazyReward = new String(Files.readAllBytes(Paths.get("data/json/crazyReward.json")),
            StandardCharsets.UTF_8);
    CrazyRewardData.loadJson(crazyReward);

    String achievement = new String(Files.readAllBytes(Paths.get("data/json/achievement.json")),
            StandardCharsets.UTF_8);
    AchievementData.loadJson(achievement);

    String mission = new String(Files.readAllBytes(Paths.get("data/json/mission.json")),
            StandardCharsets.UTF_8);
    MissionData.loadJson(mission);

    String dailyGift = new String(Files.readAllBytes(Paths.get("data/json/dailyGift.json")),
            StandardCharsets.UTF_8);
    DailyGiftData.loadJson(dailyGift);

    String vipGift = new String(Files.readAllBytes(Paths.get("data/json/vipGift.json")),
            StandardCharsets.UTF_8);
    VipGiftData.loadJson(vipGift);

    String giftCard = new String(Files.readAllBytes(Paths.get("data/json/giftCard.json")),
            StandardCharsets.UTF_8);
    GiftCardData.loadJson(giftCard);

    String shop = new String(Files.readAllBytes(Paths.get("data/json/shop.json")),
            StandardCharsets.UTF_8);
    ShopData.loadJson(shop);

    String itemMerge = new String(Files.readAllBytes(Paths.get("data/json/itemMerge.json")),
            StandardCharsets.UTF_8);
    ItemMergeData.loadJson(itemMerge);

    String event = new String(Files.readAllBytes(Paths.get("data/json/event.json")),
            StandardCharsets.UTF_8);
    EventData.loadJson(event);

    String rank = new String(Files.readAllBytes(Paths.get("data/json/rankingReward.json")),
            StandardCharsets.UTF_8);
    RankingData.loadJson(rank);

    WordFilter.loadJson("");
  }
}