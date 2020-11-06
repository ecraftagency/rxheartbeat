package com.gateway;

import com.common.LOG;
import com.common.Utilities;
import com.gateway.controller.*;
import com.common.Constant;
import com.google.gson.reflect.TypeToken;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HBGateway extends AbstractVerticle {
  public static ClusterManager  mgr;
  public static EventBus        eventBus;
  public static List<String>    nodeIps;
  public static String          localIp;
  public static JsonObject      localConfig;

  public static void main(String[] args) throws IOException {
    String config           = new String(Files.readAllBytes(Paths.get("nodes.json")));
    localConfig             = new JsonObject(config);
    Type listOfString       = new TypeToken<List<String>>() {}.getType();
    nodeIps                 = Utilities.gson.fromJson(localConfig.getJsonArray("nodes").toString(), listOfString);
    localIp                 = localConfig.getString("localIP");

    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");

    Config clusterOption = new Config();
    NetworkConfig network = clusterOption.getNetworkConfig();
    JoinConfig join = network.getJoin();
    join.getMulticastConfig().setEnabled(false);//.setMulticastGroup("172.31.32.0").setMulticastPort(143);
    for (String node : nodeIps)
      join.getTcpIpConfig().addMember(node);
    join.getTcpIpConfig().setEnabled(true);

    mgr = new HazelcastClusterManager(clusterOption);
    VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost(localIp);

    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        eventBus = vertx.eventBus();
        vertx.deployVerticle(HBGateway.class.getName());
        System.out.println("HB Gateway Deployed, local IP: " + localIp);
      }
      else {
        LOG.globalException("gateway", "verticleDeploy", res.cause());
      }
    });
  }

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.get("/nodes").handler(new NodeController());
    router.get("/api/getrole").handler(new GetRoleController());
    router.get("/exchange").handler(new MobiWebPayment());
    router.get("/iapexchange").handler(new IAPPayment());
    router.get("/nc_exchange").handler(new NetCardExchange());

    vertx.createHttpServer().requestHandler(router).listen(80);

    String address = Constant.SYSTEM_INFO.GATEWAY_EVT_BUS;
    MessageConsumer<JsonObject> messageConsumer = eventBus.consumer(address);
    messageConsumer.handler(new InternalController());

    startPromise.complete();
  }
}