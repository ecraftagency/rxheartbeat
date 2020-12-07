import com.common.Constant;
import com.couchbase.client.java.ReactiveCluster;
import com.heartbeat.DailyStats;
import com.heartbeat.HBServer;
import com.heartbeat.db.cb.AbstractCruder;
import com.heartbeat.db.dao.ChatGroupDAO;

import java.util.HashMap;

public class StatSync {
  public static void main(String[] args) throws InterruptedException {
    HBServer.rxCluster       = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
    HBServer.rxSessionBucket = HBServer.rxCluster.bucket("vn2_sessions");
    HBServer.rxIndexBucket   = HBServer.rxCluster.bucket("vn2_index");
    HBServer.rxPersistBucket = HBServer.rxCluster.bucket("vn2_persist");
    HBServer.rxStatsBucket    = HBServer.rxCluster.bucket("vn2_stats");

    AbstractCruder<DailyStats> cbAccess = new AbstractCruder<>(DailyStats.class, HBServer.rxStatsBucket);
    cbAccess.sync("daily_stats_20201207", DailyStats.inst());
  }
}
