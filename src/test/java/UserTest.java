import com.heartbeat.common.Utilities;
import com.tulinh.TLS;
import com.tulinh.controller.WheelHistory;
import com.tulinh.controller.WheelInventory;
import com.tulinh.controller.WheelItem;
import com.tulinh.controller.WheelTurn;
import com.tulinh.dto.History;
import com.tulinh.dto.User;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

import java.util.ArrayList;

public class UserTest {
  public static void main(String[] args) {
    String json = "{\"turn\":200}";
    User user = Utilities.gson.fromJson(json, User.class);
    his(user);
    String res = Utilities.gson.toJson(user).toString();
    System.out.println(res);
  }

  public static void his(User user) {
    if (user.histories == null || user.histories.size() == 0) {
      user.histories = new ArrayList<>();
      user.histories.add(History.of(1, "abc"));
    }
    else {
      user.histories.add(History.of(1, "aaa"));
    }
  }
}
