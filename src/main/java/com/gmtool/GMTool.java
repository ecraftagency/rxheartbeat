package com.gmtool;

import com.common.LOG;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.transport.model.Node;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// A SIMPLE YET FURIOUS GM TOOL
public class GMTool extends AbstractVerticle {
  public static JsonObject                localConfig;
  public static String                    gatewayIP       = "";

  public static String                    localIP = "";
  public static ClusterManager            mgr;
  public static EventBus                  eventBus;
  public static FreeMarkerTemplateEngine  templateEngine;
  public static List<Node>                nodes;

  public static void main(String[] args) throws IOException {
    String config           = new String(Files.readAllBytes(Paths.get("gmtool.json")));
    localConfig             = new JsonObject(config);
    gatewayIP               = localConfig.getString("gatewayIP");
    localIP                 = localConfig.getString("localIP");
    nodes                   = new ArrayList<>();

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
        LOG.globalException("gmtool", "deploy verticle", res.cause());
      }
    });
  }

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  @Override
  public void start(Promise<Void> startPromise) {
    templateEngine = FreeMarkerTemplateEngine.create(vertx);
    Router router = Router.router(vertx);
    AuthProvider dummy = new DB.DummyAuthProvider();
    router.route().handler(BodyHandler.create());

    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)).setAuthProvider(dummy));
    AuthHandler authHandler = RedirectAuthHandler.create(dummy, "/login");

    router.route("/server").handler(authHandler);
    router.route("/user").handler(authHandler);
    router.route("/payment").handler(authHandler);
    router.route("/mail").handler(authHandler);
    router.route("/config").handler(authHandler);
    router.route("/ldb").handler(authHandler);
    router.route("/payment").handler(authHandler);
    router.route("/shop").handler(authHandler);
    router.route("/event").handler(authHandler);
    router.route("/stats").handler(authHandler);
    router.route("/gift").handler(authHandler);

    router.post("/login-auth").handler(FormLoginHandler.create(dummy));
    //////
    router.route().handler(CorsHandler.create(".*.")
            .allowCredentials(true)
            .allowedHeader("Access-Control-Allow-Method")
            .allowedHeader("Access-Control-Allow-Origin")
            .allowedHeader("Access-Control-Allow-Credentials")
            .allowedHeader("Content-Type")
            .allowedMethod(HttpMethod.POST));

    router.route().handler(BodyHandler.create());
    router.route().handler(StaticHandler.create());
    router.post("/api/fwd").handler(new Controller());
    router.get("/:path").handler(new Renderer());
    router.get("/").handler(c -> c.response().setStatusCode(303).putHeader("Location", "server").end());

    vertx.createHttpServer().requestHandler(router).listen(3000);
    startPromise.complete();
  }

  //nodes

  public static void setNodes(List<Node> n) {
    nodes.clear();
    nodes.addAll(n);
  }

  public static List<Node> getNodes() {
    return nodes;
  }

  public static Node getNodeById(int nodeId) {
    for (Node node : nodes)
      if (node.id == nodeId)
        return node;
    return Node.ofNullObject();
  }
}