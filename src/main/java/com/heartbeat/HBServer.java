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
import com.statics.*;
import io.reactivex.Scheduler;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.RxHelper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

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
@SuppressWarnings("unused")
public class HBServer extends AbstractVerticle {
  private static final String SRC_DIR = "/src/main/java/";
  private static final Logger LOGGER = LoggerFactory.getLogger(HBServer.class);
  public  static Cruder<Session>  cruder;
  public  static ReactiveCluster  rxCluster;
  public  static ReactiveBucket   rxSessionBucket;
  public  static ReactiveBucket   rxIndexBucket;
  public  static ReactiveBucket   rxPersistBucket;

  public  static JsonObject systemConfig;
  public  static JsonObject localConfig;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    Scheduler scheduler = RxHelper.scheduler(vertx);
    CronObservable.cronspec(scheduler, "0 0 0 * * ? *", "Asia/Ho_Chi_Minh")
      .subscribe(
        timed -> {
          SessionPool.dailyReset.run();
          LOGGER.info("execute new day task");
        },
        fault -> LOGGER.error("error new day task")
      ).dispose();

    CronObservable.cronspec(scheduler, "0 0 12,19 * * ? *", "Asia/Ho_Chi_Minh")
      .subscribe(
        timed -> {
          Constant.SCHEDULE.gameShowOpen = true;
          SessionPool.resetGameShowIdols.run();
          LOGGER.info("open game show");
        },
        fault -> LOGGER.error("error open game show task")
      ).dispose();

    CronObservable.cronspec(scheduler, "0 0 14,21 * * ? *", "Asia/Ho_Chi_Minh")
      .subscribe(
        timed -> {
          Constant.SCHEDULE.gameShowOpen = false;
          SessionPool.resetGameShowIdols.run();
          LOGGER.info("close game show");
        },
        fault -> LOGGER.error("error close game show task")
      ).dispose();
  }


  @Override
  public void start(Promise<Void> startPromise) {
    try {
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

      WordFilter.loadJson("");

      String conf             = new String(Files.readAllBytes(Paths.get("config.json")));
      localConfig             = new JsonObject(conf);

      updateConst();
      SessionPool.checkHeartBeat.run();
      GroupPool.groupSyncTask.run();
    }
    catch (Exception ioe) {
      LOGGER.error(ioe.getMessage());
      startPromise.fail(ioe);
    }

    try {
      UserFight.serverStartup();
      Constant.serverStartUp();
    }
    catch (Exception e) {
      LOGGER.error(e.getMessage());
      startPromise.fail(e);
    }

    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(ar ->{
      if (ar.succeeded()) {
        systemConfig = ar.result();
        Router router = Router.router(vertx);
        AuthController authController = new AuthController(vertx);
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

        router.post("/gm/inject").handler(new InjectController());

        router.get("/loaderio-f8c2671f6ccbeec4f3a09a972475189c/").handler(ctx ->
                ctx.response().end("loaderio-f8c2671f6ccbeec4f3a09a972475189c"));

        vertx.createHttpServer().requestHandler(router).listen(localConfig.getInteger("http.port", 8080));
        startPromise.complete();
      }
      else {
        startPromise.fail("fail to get config");
      }
    });
  }

  public static void main(String[] args) {
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
      LOGGER.info("HBServer shutdown hook");
    }));

    Vertx.vertx().deployVerticle(HBServer.class.getName());
  }

  private static void updateConst() {
    Constant.ONLINE_INFO.ONLINE_HEARTBEAT_TIME = localConfig.getInteger("ONLINE_INFO.ONLINE_HEARTBEAT_TIME");
  }
}