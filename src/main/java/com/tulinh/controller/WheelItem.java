package com.tulinh.controller;

import com.tulinh.dto.*;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tulinh.TLS.*;
import static com.tulinh.Const.*;

public class WheelItem implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(WheelItem.class);

  private long lastSync = 0;

  private Jedis agent;
  public WheelItem() {
    agent = redisPool.getResource();
  }
  @Override
  public void handle(RoutingContext ctx) {
    int currentReqCount   = requestCounter.getAndIncrement();
    final int id          = currentReqCount%SetUpHandler.nUser;
    Item randItem         = calcRnd(lsItem);
    String counterKey     = globalCounter.get(randItem.type);
    List<String> mres     = agent.mget(Integer.toString(id), counterKey);

    if (mres != null && mres.size() == 2) {
      int remainTurn, totalCounter;
      try {
        remainTurn      = Integer.parseInt(mres.get(0));
        totalCounter    = Integer.parseInt(mres.get(1));
        if (remainTurn > 0 && totalCounter < lsItem.get(randItem.type).maximum) {
          localCounter.get(randItem.type).getAndIncrement();
          agent.decr(Integer.toString(id));
          agent.incr("h" + id + "_" + randItem.type);
          agent.incr("h" + id + "_" + randItem.type);

          if (System.currentTimeMillis() - lastSync > 4000) {
            agent.incrBy("10_counter", localCounter.get(0).get());
            agent.incrBy("50_counter", localCounter.get(1).get());
            agent.incrBy("mlg_counter",localCounter.get(2).get());
            agent.incrBy("mlg_counter",localCounter.get(3).get());
            agent.incrBy("mq_counter", localCounter.get(4).get());
            agent.incrBy("mp_counter", localCounter.get(5).get());
            agent.incrBy("tlg_counter",localCounter.get(6).get());
            agent.incrBy("tln_counter",localCounter.get(7).get());
            agent.incrBy("tq_counter", localCounter.get(8).get());
            agent.incrBy("tp_counter", localCounter.get(9).get());
            for (AtomicInteger ai : localCounter.values())
              ai.set(0);
            lastSync = System.currentTimeMillis();
          }
          jsonResp(ctx, Resp.ItemOk.of(randItem.type, randItem.name, --remainTurn));
        }
        else {
          jsonResp(ctx, Resp.ItemFail.of("failed", -1));
        }
      }
      catch (Exception e) {
        LOGGER.info("nil_remain_or_counter_exception");
        ctx.response().setStatusCode(400).end("nil_remain_or_counter");
      }
    }
    else {
      LOGGER.info("get_counter_remain_fail " + id + " " + counterKey);
      ctx.response().setStatusCode(400).end("nil_remain_or_counter");
    }
  }

  private void jsonResp(RoutingContext ctx, Object jsonBody) {
    ctx.response().putHeader("Content-Type", "text/json")
            .end(Json.encode(jsonBody));
  }

  private Item calcRnd(List<Item> items) {
    int rand      = ThreadLocalRandom.current().nextInt(1, 1000000 + 1);
    int percent   = 0;
    Item res      = null;

    for (Item item : items) {
      percent += item.percent;
      if (rand < percent) {
        res = item;
        break;
      }
    }

    if (res == null || res.amount >= res.maximum)
      res = items.get(3);

    res.amount += 1;
    return res;
  }
}