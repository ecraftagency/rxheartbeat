import com.couchbase.client.java.ReactiveCluster;
import com.heartbeat.HBServer;
import com.heartbeat.common.Constant;
import com.heartbeat.db.DataAccess;
import com.heartbeat.db.impl.CBBadge;
import com.heartbeat.db.impl.CBSession;
import com.heartbeat.model.Session;

public class CBAccess {
  public static void main(String[] args) {
    HBServer.rxCluster       = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
    HBServer.rxSessionBucket = HBServer.rxCluster.bucket("sessions");
    HBServer.rxIndexBucket   = HBServer.rxCluster.bucket("index");

//    DataAccess<Session> da = CBSession.getInstance();
//    da.map("100001", "hello", ar -> {
//      if (ar.succeeded()) {
//        System.out.println("ok");
//        da.unmap("hello", arr -> {
//          if (arr.succeeded())
//            System.out.println("ok");
//          else
//            System.out.println(ar.cause().getMessage());
//        });
//      }
//      else
//        System.out.println(ar.cause().getMessage());
//    });
//
//    System.out.println(da.map("2231","asdasda"));
//    System.out.println(da.unmap("asdasda"));

    DataAccess<CBBadge.Title> ta = CBBadge.getInstance();
//    CBBadge.Title title = CBBadge.Title.of("1000001", "mrstart", "stalin is god");
//    System.out.println(ta.add("numberone", title));

    CBBadge.Title title = ta.load("numberone");
    System.out.println(title);
  }
}
