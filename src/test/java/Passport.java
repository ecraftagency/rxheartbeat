import com.common.Constant;
import com.common.Utilities;
import com.heartbeat.Passport100D;
import io.vertx.core.*;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.web.client.WebClient;

public class Passport extends AbstractVerticle {
  public static ClusterManager mgr;

  public static void main(String[] args) throws Exception {
    String jwtAuth = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwbGF5ZXJfaWQiOiI1ZjU4NGI1OTgyN2U2YzYwOGM5YmQyNTQiLCJhcHBfaWQiOiI1ZjU3MmMyZmUyYjMxNjYwOTJkMjA3NWIiLCJ1c2VybmFtZSI6ImdiY2YyZTllNGE1IiwiaWF0IjoxNTk5NjIxOTc3fQ.zYnfAcxEFOO92ihx8vg9wjCfh5YhfXyX28Om2y4WPJg";
    int timeStamp = (int)(System.currentTimeMillis()/1000);
    String sign = Utilities.md5Encode(Utilities.md5Encode(jwtAuth + timeStamp) + Constant.PASSPORT.ENV.secret);

    String param = String.format(Constant.PASSPORT.ENV.paramFormat, jwtAuth, timeStamp, sign);
    String req = Constant.PASSPORT.ENV.host + "/player/verify_v2" + param;
    System.out.println(req);
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Passport());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Passport100D.webClient = WebClient.create(vertx);
    String jwtAuth = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwbGF5ZXJfaWQiOiI1ZjU4NGI1OTgyN2U2YzYwOGM5YmQyNTQiLCJhcHBfaWQiOiI1ZjU3MmMyZmUyYjMxNjYwOTJkMjA3NWIiLCJ1c2VybmFtZSI6ImdiY2YyZTllNGE1IiwiaWF0IjoxNTk5NjIxOTc3fQ.zYnfAcxEFOO92ihx8vg9wjCfh5YhfXyX28Om2y4WPJg";
    Passport100D.verify(jwtAuth, ar -> {
      if (ar.succeeded()) {
        System.out.println(ar.result().player_id);
      }
      else {
        System.out.println(ar.cause().getMessage());
      }
    });
  }
}
