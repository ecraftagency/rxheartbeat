package com.tulinh.controller;

import com.heartbeat.common.Utilities;
import com.tulinh.Const;
import com.tulinh.dto.Item;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.tulinh.Const.*;

import static com.tulinh.TLS.*;
public class SetUpHandler implements Handler<RoutingContext> {
  private Jedis userAgent;
  private Jedis cntAgent;

  public SetUpHandler() {
    userAgent = redisPool.getResource();
    cntAgent = redisPool.getResource();
    userAgent.select(0);
    cntAgent.select(1);
  }

  @Override
  public void handle(RoutingContext ctx) {
    nUSER           = ctx.getBodyAsJson().getInteger("nUser");
    SYNC_INTERVAL   = ctx.getBodyAsJson().getInteger("syncInterval");
    String strItems = ctx.getBodyAsJson().getJsonArray("items").toString();
    staticItems     = Arrays.asList(Utilities.gson.fromJson(strItems, Item[].class));
    SYNC_MODE       = ctx.getBodyAsJson().getBoolean("syncMode");

    userAgent.flushAll();
    cntAgent.flushAll();

    List<String> batch      = new ArrayList<>();

    for (String counterKey : Const.globalCounter.values()) {
      batch.add(counterKey);
      batch.add("0");
    }

    cntAgent.mset(batch.toArray(new String[]{}));
    batch.clear();

    if (!SYNC_MODE) {
      for (int i = 0; i < nUSER; i++) {
        JsonObject json = new JsonObject();
        json.put("turn", 5000000);
        for (int j = 0; j < 10; j++) {
          json.put("i" + j, 0);
        }
        batch.add(Integer.toString(i));
        batch.add(json.toString());
      }
    }
    else {
      for (int i = 0; i < nUSER; i++) {
        batch.add(Integer.toString(i));
        batch.add("5000000");
      }
    }

    userAgent.mset(batch.toArray(new String[]{}));

    ctx.response().end("duy");
  }
}