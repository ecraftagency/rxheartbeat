package com.tulinh.controller;

import com.tulinh.Const;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

import static com.tulinh.TLS.*;
public class SetUpHandler implements Handler<RoutingContext> {
  public static int         nUser = 5000;
  private static final int  nType = 10;
  private Jedis agent;

  public SetUpHandler() {
    agent = redisPool.getResource();
  }

  @Override
  public void handle(RoutingContext ctx) {
    String snUser = ctx.request().getParam("nUser");
    nUser         = Integer.parseInt(snUser);

    agent.flushAll();
    for (String counterKey : Const.globalCounter.values())
      agent.set(counterKey, "0");

    List<String> batch = new ArrayList<>();
    try {
      for (int i = 0; i < nUser; i++) {
        for (int t = 0; t < nType; t++) {
          batch.add("h" + i + "_" + t);
          batch.add("0");
          batch.add("i" + i + "_" + t);
          batch.add("0");
        }
        batch.add(Integer.toString(i));
        batch.add("5000000");
      }
      agent.mset(batch.toArray(new String[]{}));
      ctx.response().end("ok");
    }
    catch (Exception e) {
      ctx.response().end(e.getMessage());
    }
  }
}