package com.tulinh.controller;

import com.tulinh.Const;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

import static com.tulinh.TLS.redisPool;

public class CounterHandler implements Handler<RoutingContext> {
  private Jedis agent;
  private List<String> batch;

  public CounterHandler() {
    agent = redisPool.getResource();
    batch = new ArrayList<>();
    batch.addAll(Const.globalCounter.values());
  }
  @Override
  public void handle(RoutingContext ctx) {
    List<String> mres = agent.mget(batch.toArray(new String[]{}));
    JsonObject resp = new JsonObject();
    resp.put("10_counter", mres.get(0));
    resp.put("50_counter", mres.get(1));
    resp.put("mlg_counter", mres.get(2));
    resp.put("mln_counter", mres.get(3));
    resp.put("mq_counter", mres.get(4));
    resp.put("mp_counter", mres.get(5));
    resp.put("tlg_counter", mres.get(6));
    resp.put("tln_counter", mres.get(7));
    resp.put("tq_counter", mres.get(8));
    resp.put("tp_counter", mres.get(9));
    ctx.response().putHeader("Content-Type", "text/json")
            .end(Json.encode(resp));
  }
}
