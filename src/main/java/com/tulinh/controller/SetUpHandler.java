package com.tulinh.controller;

import com.heartbeat.common.Utilities;
import com.tulinh.Const;
import com.tulinh.dto.Item;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.tulinh.Const.*;

import static com.tulinh.TLS.*;
public class SetUpHandler implements Handler<RoutingContext> {
  private static final int  nType = 10;
  private Jedis             agent;

  public SetUpHandler() {
    agent = redisPool.getResource();
  }

  @Override
  public void handle(RoutingContext ctx) {
    nUSER           = ctx.getBodyAsJson().getInteger("nUser");
    SYNC_INTERVAL   = ctx.getBodyAsJson().getInteger("syncInterval");
    String strItems = ctx.getBodyAsJson().getJsonArray("items").toString();
    staticItems     = Arrays.asList(Utilities.gson.fromJson(strItems, Item[].class));

    agent.flushAll();

    List<String> batch = new ArrayList<>();
    for (String counterKey : Const.globalCounter.values()) {
      batch.add(counterKey);
      batch.add("0");
    }

    for (int i = 0; i < nUSER; i++) {
      batch.add(Integer.toString(i));
      batch.add("5000000");
    }
    agent.mset(batch.toArray(new String[]{}));
    ctx.response().end(staticItems.get(0).name);
  }
}