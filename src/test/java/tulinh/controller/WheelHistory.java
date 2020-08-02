package tulinh.controller;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import redis.clients.jedis.Jedis;

import java.util.List;

import static tulinh.TLS.redisPool;

public class WheelHistory implements Handler<RoutingContext> {
  private Jedis agent;

  public WheelHistory() {
    agent = redisPool.getResource();
  }
  @Override
  public void handle(RoutingContext ctx) {
    String megaID = ctx.request().getParam("megaID");
    List<String> history = agent.lrange("user:" + megaID + ":history", 0, -1);
    JsonArray resp = new JsonArray();
    if (history != null) {
      history.forEach(resp::add);
    }
    ctx.response().putHeader("Content-Type", "text/json")
            .end(Json.encode(resp));
  }
}
