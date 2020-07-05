import com.heartbeat.db.impl.CBDataAccess;
import com.heartbeat.model.Session;

public class DataAccess {
  public static void main(String[] args) {
    com.heartbeat.db.DataAccess<Session> da = CBDataAccess.getInstance();
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

    System.out.println(da.map("2231","asdasda"));
    System.out.println(da.unmap("asdasda"));
  }
}
