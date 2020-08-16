package com.gateway;

import com.gateway.controller.InternalController;
import com.common.Constant;
import com.gateway.controller.NodeController;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBGateway extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HBGateway.class);

  public static ClusterManager  mgr;
  public static EventBus        eventBus;

  public static void main(String[] args) {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");

    Config conf = new Config();
    NetworkConfig network = conf.getNetworkConfig();
    JoinConfig join = network.getJoin();
    join.getMulticastConfig().setEnabled(false);
    join.getTcpIpConfig().addMember("172.31.38.195").setEnabled(true);
    //network.getInterfaces().setEnabled(true).addInterface("172.31.*.*");

    mgr = new HazelcastClusterManager(conf);
    VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost("172.31.37.156");

    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        eventBus = vertx.eventBus();
        vertx.deployVerticle(HBGateway.class.getName());
        System.out.println("HB Gateway Deployed");
      }
      else {
        LOGGER.error(res.cause().getMessage());
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
    vertx.createHttpServer().requestHandler(router).listen(80);

    String address = Constant.SYSTEM_INFO.GATEWAY_EVT_BUS;
    MessageConsumer<JsonObject> messageConsumer = eventBus.consumer(address);
    messageConsumer.handler(new InternalController());

    startPromise.complete();
  }
}