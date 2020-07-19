package com.tulinh.controller;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import redis.clients.jedis.Jedis;

import static com.tulinh.TLS.redisPool;

public class WheelInventory implements Handler<RoutingContext> {
  private Jedis agent;

  public WheelInventory() {
    agent = redisPool.getResource();
  }
  @Override
  public void handle(RoutingContext ctx) {
    String megaID = ctx.request().getParam("megaID");

    JsonArray resp = new JsonArray();
    for (int i = 0; i < 10; i++) {
      String key = "user:" + megaID + ":inventory:" + i;
      String val = agent.get(key);
      if (val == null)
        val = "0";
      resp.add(val);
    }

    ctx.response().putHeader("Content-Type", "text/json")
            .end(Json.encode(resp));
  }
}
