package verticles;

import com.hazelcast.config.Config;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class SenderLauncher {
  public static ClusterManager mgr;

  public static void main(String[] args) {
    Config hazelcastConfig = new Config();

    hazelcastConfig.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1").setEnabled(true);
    hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);

    mgr = new HazelcastClusterManager(hazelcastConfig);

    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(2);
        res.result().deployVerticle(SenderVerticle.class.getName(), deploymentOptions);
        System.out.println("Sender Verticle deployed");
      }
    });
  }
}
