package tulinh.controller;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import tulinh.dto.Item;
import tulinh.dto.Resp;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static tulinh.TLS.*;
import static tulinh.Const.*;

public class ShardWheelItem implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ShardWheelItem.class);
  private Jedis userAgent;
  private Jedis cntAgent;

  public ShardWheelItem() {
    userAgent = redisPool.getResource();
    cntAgent  = redisPool.getResource();
    userAgent.select(0);
    cntAgent.select(1);
  }

  @Override
  public void handle(RoutingContext ctx) {
    int currentReqCount = requestCounter.getAndIncrement();
    String id           = Integer.toString(currentReqCount% nUSER);
    Item randItem       = calcRnd(staticItems);
    String counterKey   = globalCounter.get(randItem.type);
    String res          = userAgent.get(id);
    long curMs          = System.currentTimeMillis();

    if (res != null) {
      JsonObject user   = new JsonObject(res);
      int remainTurn    = user.getInteger("turn");
      int inv           = user.getInteger("i" + randItem.type);

      try {
        if (remainTurn > 0) {
          String rm       = cntAgent.get(counterKey);
          int totalCount  = Integer.parseInt(rm);
          if (totalCount < staticItems.get(randItem.type).maximum) {
            user.put("turn", --remainTurn);
            user.put("i" + randItem.type, ++inv);
            userAgent.set(id, user.toString());
            userAgent.rpush("user:" + id + ":history", curMs + "_" + randItem.type);
            cntAgent.incr(counterKey);
            jsonResp(ctx, user);
          }
          else {
            jsonResp(ctx, Resp.ItemFail.of("failed", -1));
          }
        }
        else {
          jsonResp(ctx, Resp.ItemFail.of("failed", -1));
        }
      }
      catch (Exception e) {
        LOGGER.info("nil_remain_or_counter_exception");
        ctx.response().setStatusCode(400).end("nil_remain_or_counter");
      }
    } else {
      LOGGER.info("get_counter_remain_fail " + id);
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