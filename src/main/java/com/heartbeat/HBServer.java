package com.heartbeat;

import com.common.LOG;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.ReactiveCluster;
import com.common.Constant;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.heartbeat.controller.*;
import com.heartbeat.db.Cruder;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.internal.InternalController;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.*;
import com.heartbeat.scheduler.TaskRunner;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static com.common.Constant.*;

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
  public static Cruder<Session>  cruder;
  public static ReactiveCluster  rxCluster;
  public static ReactiveBucket   rxSessionBucket;
  public static ReactiveBucket   rxIndexBucket;
  public static ReactiveBucket   rxPersistBucket;

  public static JsonObject       systemConfig;
  public static JsonObject       localConfig;
  public static WorkerExecutor   executor;

//  public static Disposable        gsOpenTask;
//  public static Disposable        gsCloseTask;
//  public static Disposable        newDayTask;
//  public static long              gateWayPingTaskId;

  public static ClusterManager    mgr;
  public static EventBus          eventBus;

  public static String  nodeIp    = "";
  public static int     nodePort  = 0;
  public static String  nodeBus   = "";
  public static int     nodeId    = 0;
  public static String  nodeName  = "";
  public static String  gatewayIP = "";
  public static String  localIP   = "";

  public static void main(String[] args) throws IOException {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    String conf             = new String(Files.readAllBytes(Paths.get("config.json")));
    localConfig             = new JsonObject(conf);
    overrideConstant();

    rxCluster       = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
    rxSessionBucket = HBServer.rxCluster.bucket(String.format("%s%d_%s", DB.BUCKET_PREFIX, nodeId, DB.SESSION_BUCKET));
    rxIndexBucket   = HBServer.rxCluster.bucket(String.format("%s%d_%s", DB.BUCKET_PREFIX, nodeId, DB.INDEX_BUCKET));
    rxPersistBucket = HBServer.rxCluster.bucket(String.format("%s%d_%s", DB.BUCKET_PREFIX, nodeId, DB.PERSIST_BUCKET));

    //for faster startup, fucking couchbase java sdk T___T
    cruder = CBSession.getInstance();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      SessionPool.removeAll();
      GroupPool.removeAll();
      UserLDB.syncLDBToDB(LEADER_BOARD.TALENT_LDB_ID);
      UserLDB.syncLDBToDB(LEADER_BOARD.FIGHT_LDB_ID);
      UserInventory.syncItemStatToDB();
      LOG.console("HBServer shutdown hook");
    }));

    Config clusterOption = new Config();
    NetworkConfig network = clusterOption.getNetworkConfig();
    JoinConfig join = network.getJoin();
    join.getMulticastConfig().setEnabled(false);
    join.getTcpIpConfig().addMember(gatewayIP).setEnabled(true);

    mgr = new HazelcastClusterManager(clusterOption);

    VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost(localIP);

    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        eventBus = vertx.eventBus();
        //scheduleTask(vertx);
        TaskRunner.getInstance().setVXInstance(vertx);
        TaskRunner.getInstance().scheduleMainTask();
        vertx.deployVerticle(HBServer.class.getName());
        LOG.console("HB Server Deployed");
      }
    });
  }

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  @Override
  public void start(Promise<Void> startPromise) {
    try {
      StaticLoader.loadStaticData();

      SessionPool.checkHeartBeat.run();
      GroupPool.groupSyncTask.run();

      UserFight.serverStartup();

      UserLDB.loadLDBFromDB(LEADER_BOARD.TALENT_LDB_ID);
      UserLDB.loadLDBFromDB(LEADER_BOARD.FIGHT_LDB_ID);
      UserInbox.loadInboxFromDB();
      UserInventory.loadItemStatsFromDB();

      HBServer.executor = vertx.createSharedWorkerExecutor("worker-pool");
    }
    catch (Exception ioe) {
      startPromise.fail(ioe);
      LOG.globalException("node", "verticleStartUp", ioe);
    }

    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(ar -> {
      if (ar.succeeded()) {
        systemConfig = ar.result();
        
        Passport100D.webClient = WebClient.create(vertx);

        MessageConsumer<JsonObject> messageConsumer = eventBus.consumer(nodeBus);
        messageConsumer.handler(new InternalController());

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
        router.post("/api/net_award").handler(new NetAwardController());
        router.post("/api/group").handler(new GroupController());
        router.post("/api/daily_mission").handler(new DailyMissionController());
        router.post("/api/achievement").handler(new AchievementController());
        router.post("/api/mission").handler(new MissionController());
        router.post("/api/rollcall").handler(new RollCallController());
        router.post("/api/event").handler(new EventController());
        router.post("/api/ranking").handler(new RankingController());
        router.post("/api/leaderboard").handler(new LeaderBoardController());
        router.post("/api/inbox").handler(new InboxController());
        router.post("/api/payment").handler(new PaymentController());

        router.get("/loaderio-f8c2671f6ccbeec4f3a09a972475189c/").handler(ctx ->
                ctx.response().end("loaderio-f8c2671f6ccbeec4f3a09a972475189c"));

        HttpServerOptions options = new HttpServerOptions().setSsl(true).setKeyStoreOptions(
                new JksOptions().setPath("keystore.jks").setPassword("changeit")
        );
        vertx.createHttpServer(options)
                .requestHandler(router).listen(nodePort);

        startPromise.complete();
      }
      else {
        startPromise.fail("fail to get config");
      }
    });
  }

  private static void overrideConstant() {
    nodeId                                 = localConfig.getInteger("NODE.ID");
    nodeIp                                 = localConfig.getString("NODE.IP");
    nodePort                               = localConfig.getInteger("NODE.PORT");
    nodeName                               = localConfig.getString("NODE.NAME");
    gatewayIP                              = localConfig.getString("GATEWAY_IP");
    localIP                                = localConfig.getString("NODE.LOCAL_IP");
    DB.HOST                                = localConfig.getString("DB.HOST");
    DB.USER                                = localConfig.getString("DB.USER");
    DB.PWD                                 = localConfig.getString("DB.PWD");
    ONLINE_INFO.ONLINE_HEARTBEAT_TIME      = localConfig.getInteger("ONLINE_INFO.ONLINE_HEARTBEAT_TIME");
    TIME_ZONE                              = localConfig.getString("SCHEDULE.TIMEZONE");
    if (nodeId > 0) {
      DB.ID_INIT                           = nodeId*1000000;
      DB.GID_INIT                          = nodeId*10000;
      nodeBus                              = String.format("%d.%s.bus", nodeId, nodeName);
    }
    else {
      LOG.globalException("node", "overrideConstant", String.format("critical invalid node id: %d", nodeId));
    }
  }
}