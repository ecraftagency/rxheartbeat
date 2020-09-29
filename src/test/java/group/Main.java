package group;

import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.ReactiveQueryResult;
import com.heartbeat.HBServer;
import com.common.Constant;
import com.common.GlobalVariable;
import com.common.Utilities;
import com.heartbeat.model.Session;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] args) {
    HBServer.rxCluster       = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
    HBServer.rxSessionBucket = HBServer.rxCluster.bucket("sessions");
    HBServer.rxIndexBucket   = HBServer.rxCluster.bucket("index");
    HBServer.rxPersistBucket = HBServer.rxCluster.bucket("persist");
    fetchGroups(ar -> {
      if (ar.succeeded())
        System.out.println(ar.result());
    });
    GlobalVariable.schThreadPool.scheduleAtFixedRate(() -> {}, 0, 1000000, TimeUnit.MINUTES);
  }

  public static void fetchGroups(Handler<AsyncResult<String>> handler) {
    HBServer.rxCluster.query("SELECT * from `persist` WHERE docType = \"group\"")
      .flatMapMany(ReactiveQueryResult::rowsAsObject).collectList().subscribe(
        ar -> {
          StringBuilder builder = GlobalVariable.stringBuilder.get();
          builder.append("[");
          for (int i = 0; i < ar.size(); i++) {
            JsonObject row = ar.get(i);
            builder.append(row.getObject("persist"));
            if (i < ar.size() - 1)
              builder.append(",");
          }
          builder.append("]");
          handler.handle(Future.succeededFuture(builder.toString()));
        },
        er -> {
          handler.handle(Future.succeededFuture("[]"));
        }
    );
  }

  public static void groupInfo(Session session) {
//    session.loadSessionGroup(ar -> {
//      if (ar.succeeded())
//        System.out.println(Utilities.gson.toJson(ar.result()));
//      else
//        System.out.println(ar.cause().getMessage());
//    });
  }

  public static void createGroup(Session session) {

  }
}
