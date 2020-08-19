package com.gmtool;

import com.common.LOG;
import com.gmtool.controller.UserController;
import com.gmtool.handler.IndexHandler;
import com.gmtool.handler.UserHandler;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.heartbeat.HBServer;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// A FAST AND FURIOUS GM TOOL
public class GMTool extends AbstractVerticle {
  public static JsonObject                localConfig;
  public static String                    gatewayIP = "";
  public static String                    localIP = "";
  public static ClusterManager            mgr;
  public static EventBus                  eventBus;
  public static FreeMarkerTemplateEngine  templateEngine;

  public static void main(String[] args) throws IOException {
    String config           = new String(Files.readAllBytes(Paths.get("gmtool.json")));
    localConfig             = new JsonObject(config);
    gatewayIP               = localConfig.getString("gatewayIP");
    localIP                 = localConfig.getString("localIP");

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
        vertx.deployVerticle(GMTool.class.getName());
        System.out.println("HB GMTool Deployed, local IP: " + localIP);
      }
      else {
        LOG.globalException(res.cause());
      }
    });
  }

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    templateEngine = FreeMarkerTemplateEngine.create(vertx);
    HBServer.loadStaticData();
    Router router = Router.router(vertx);

    router.route().handler(CorsHandler.create(".*.")
            .allowCredentials(true)
            .allowedHeader("Access-Control-Allow-Method")
            .allowedHeader("Access-Control-Allow-Origin")
            .allowedHeader("Access-Control-Allow-Credentials")
            .allowedHeader("Content-Type")
            .allowedMethod(HttpMethod.POST));

    router.route().handler(BodyHandler.create());
    router.route().handler(StaticHandler.create());
    router.get("/").handler(new IndexHandler());
    router.get("/user").handler(new UserHandler());
    router.post("/api/user").handler(new UserController());

    vertx.createHttpServer().requestHandler(router).listen(3000);
    startPromise.complete();
  }
}