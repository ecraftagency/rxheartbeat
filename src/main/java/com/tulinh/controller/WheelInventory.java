package com.tulinh.controller;

import com.tulinh.Const;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

import static com.tulinh.TLS.redisPool;

public class WheelInventory implements Handler<RoutingContext> {
  private Jedis agent;
  private List<String> batch;

  public WheelInventory() {
    agent = redisPool.getResource();
    batch = new ArrayList<>();
    batch.addAll(Const.globalCounter.values());
  }
  @Override
  public void handle(RoutingContext ctx) {
    String megaID = ctx.request().getParam("megaID");

    JsonArray resp = new JsonArray();
    for (int i = 0; i < 10; i++) {
      String key = "i" + megaID + "_" + i;
      String val = agent.get(key);
      if (val == null)
        val = "0";
      resp.add(val);
    }

    ctx.response().putHeader("Content-Type", "text/json")
            .end(Json.encode(resp));
  }
}
