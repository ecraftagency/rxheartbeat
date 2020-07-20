import com.heartbeat.common.Utilities;
import com.statics.VipData;
import com.tulinh.Const;
import com.tulinh.TLS;
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
    Const.staticItems.forEach(item -> System.out.println(Utilities.gson.toJson(item)));

  }
}
