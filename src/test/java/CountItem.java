import com.common.Constant;
import com.common.GlobalVariable;
import com.common.Utilities;
import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.query.ReactiveQueryResult;
import com.heartbeat.HBServer;
import com.heartbeat.model.Session;
import com.transport.model.MailObj;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CountItem {
  public static void main(String[] args) {
    HBServer.rxCluster       = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
    HBServer.rxSessionBucket = HBServer.rxCluster.bucket("vn2_sessions");
    countItem(ar -> {});
    GlobalVariable.schThreadPool.scheduleAtFixedRate(() -> {}, 0, 1000000, TimeUnit.MINUTES);

  }

  public static void countItem(Handler<AsyncResult<Map<Integer, Integer>>> handler) {
    Map<Integer, Integer> res = new HashMap<>();
    HBServer.rxCluster.query("SELECT * from `vn2_sessions`")
            .flatMapMany(ReactiveQueryResult::rowsAsObject)
            .map(json -> Utilities.gson.fromJson(json.get("vn2_sessions").toString(), Session.class)).collectList()
            .subscribe(
                    s -> s.forEach(session -> {
                      System.out.println(session.userInventory.userItems.get(124));
                    }),
                    e -> System.out.println(e.getMessage())
            );
  }
}
