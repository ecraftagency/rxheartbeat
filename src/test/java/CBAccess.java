import com.couchbase.client.java.ReactiveCluster;
import com.heartbeat.HBServer;
import com.heartbeat.common.Constant;
import com.heartbeat.db.DataAccess;
import com.heartbeat.db.cb.CBCounter;
import com.heartbeat.db.cb.CBTitle;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.Session;
import com.transport.model.Title;

public class CBAccess {
  public static void main(String[] args) {
    HBServer.rxCluster       = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
    HBServer.rxSessionBucket = HBServer.rxCluster.bucket("sessions");
    HBServer.rxIndexBucket   = HBServer.rxCluster.bucket("index");
    long id = CBCounter.getInstance().increase("HeartBeatOnlineUserID", 100000);
    System.out.println(id);
  }

  public static void titleInsert() {
    DataAccess<Title> ta = CBTitle.getInstance();

    Title title = Title.of("1000001", "mrstart", "stalin is god");
    System.out.println(ta.add("numberone", title));

    title = ta.load("numberone");
    System.out.println(title);
  }

  public static void mapTest() {
    DataAccess<Session> da = CBSession.getInstance();
    da.map("100001", "hello", ar -> {
      if (ar.succeeded()) {
        System.out.println("ok");
        da.unmap("hello", arr -> {
          if (arr.succeeded())
            System.out.println("ok");
          else
            System.out.println(ar.cause().getMessage());
        });
      }
      else
        System.out.println(ar.cause().getMessage());
    });
  }
}
