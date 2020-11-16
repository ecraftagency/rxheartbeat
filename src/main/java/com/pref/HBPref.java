package com.pref;

import com.common.Constant;
import com.common.LOG;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.ReactiveCluster;
import com.gmtool.GMTool;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*upstream identity manager*/
public class HBPref extends AbstractVerticle {
  public static JsonObject                        localConfig;
  public static String                            gatewayIP       = "";
  public static String                            localIP         = "";
  public static ClusterManager                    mgr;
  public static EventBus                          eventBus;
  public static ReactiveCluster                   rxCluster;
  public static Map<Integer,ReactiveBucket>       refBuckets;
  public static HashSet<Integer>                  shardBucketIds;

  public static void main(String[] args) throws IOException {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
    String config           = new String(Files.readAllBytes(Paths.get("pref.json")));
    localConfig             = new JsonObject(config);
    refBuckets              = new HashMap<>();
    overrideConstant();

    Config clusterOption = new Config();
    NetworkConfig network = clusterOption.getNetworkConfig();
    JoinConfig join = network.getJoin();
    join.getMulticastConfig().setEnabled(false);
    join.getTcpIpConfig().addMember(gatewayIP).setEnabled(true);

    mgr           = new HazelcastClusterManager(clusterOption);
    rxCluster     = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
    for (Integer bucketId : shardBucketIds) {
      refBuckets.put(bucketId, rxCluster.bucket(String.format("ref_%d", bucketId)));
    }

    VertxOptions options = new VertxOptions().setClusterManager(mgr).setClusterHost(localIP);

    Vertx.clusteredVertx(options, res -> {
      if (res.succeeded()) {
        Vertx vertx = res.result();
        eventBus = vertx.eventBus();
        vertx.deployVerticle(HBPref.class.getName());
        System.out.println("HB Pref Deployed, local IP: " + localIP);
      }
      else {
        LOG.globalException("pref", "deploy verticle", res.cause());
      }
    });
  }

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
  }

  @Override
  public void start(Promise<Void> startPromise) {
    String address = Constant.SYSTEM_INFO.PREF_EVT_BUS;
    MessageConsumer<JsonObject> messageConsumer = eventBus.consumer(address);
    messageConsumer.handler(new InternalController());
    startPromise.complete();
  }

  private static void overrideConstant() {
    gatewayIP            = localConfig.getString("gatewayIP");
    localIP              = localConfig.getString("localIP");
    Constant.DB.HOST     = localConfig.getString("DB.HOST");
    Constant.DB.USER     = localConfig.getString("DB.USER");
    Constant.DB.PWD      = localConfig.getString("DB.PWD");

    shardBucketIds       = new HashSet<>();
    shardBucketIds.add(1);
    shardBucketIds.add(2);
    shardBucketIds.add(3);
    shardBucketIds.add(4);
    shardBucketIds.add(5);
    shardBucketIds.add(6);
    shardBucketIds.add(7);
    shardBucketIds.add(8);
  }
}