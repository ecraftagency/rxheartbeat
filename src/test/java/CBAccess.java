import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.query.ReactiveQueryResult;
import com.heartbeat.HBServer;
import com.heartbeat.common.Constant;
import com.heartbeat.db.Cruder;
import com.heartbeat.db.Mapper;
import com.heartbeat.db.cb.CBMapper;
import com.heartbeat.db.cb.CBTitle;
import com.transport.model.Title;
import reactor.core.publisher.Mono;

public class CBAccess {
  public static void main(String[] args) throws InterruptedException {
    HBServer.rxCluster       = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
    HBServer.rxSessionBucket = HBServer.rxCluster.bucket("sessions");
    HBServer.rxIndexBucket   = HBServer.rxCluster.bucket("index");

//    System.out.println("start");
//      HBServer.rxSessionBucket.defaultCollection().lookupIn("100017", Collections.singletonList(
//              exists("groups")
//      )).subscribe(res -> {
//        if (res.exists(0)) {
//
//          HBServer.rxSessionBucket.defaultCollection().mutateIn("100017", Collections.singletonList(
//                  arrayAddUnique("groups", Collections.singletonList(200))
//          )).subscribe(ress -> System.out.println(res.toString()),
//                  errr -> System.out.println(errr.getMessage()));
//        }
//      });

    Mono<ReactiveQueryResult> result = HBServer.rxCluster
            .query("select * from `index`");

    result.flatMapMany(ReactiveQueryResult::rowsAsObject)
          .subscribe(row -> {
            System.out.println("Found row: " + row.toString());
          });
    Thread.sleep(40000);
//      if (ra != null)
//        System.out.println(ra.toString().contains("SUCCESS"));

  }

  public static void titleInsert() {
    Cruder<Title> ta = CBTitle.getInstance();

    Title title = Title.of("1000001", "","mrstart", "stalin is god");
    System.out.println(ta.add("numberone", title));

    title = ta.load("numberone");
    System.out.println(title);
  }

  public static void mapTest() {
    Mapper da = CBMapper.getInstance();
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