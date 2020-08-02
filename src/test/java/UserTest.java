import com.heartbeat.common.Utilities;
import tulinh.Const;

public class UserTest {
  public static void main(String[] args) {
    Const.staticItems.forEach(item -> System.out.println(Utilities.gson.toJson(item)));

  }
}
