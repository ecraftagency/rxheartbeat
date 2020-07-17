package com.tulinh.controller;

import com.heartbeat.common.Utilities;
import com.tulinh.TLS;
import com.tulinh.config.ItemConfig;
import com.tulinh.dto.*;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class WheelItem implements Handler<RoutingContext> {
  private static Map<Integer, String> tileMap;
  AtomicInteger incrementer;
  static {
    tileMap = new HashMap<>();
    tileMap.put(0, "thecao10");
    tileMap.put(1, "thecao50");
    tileMap.put(2, "mlong");
    tileMap.put(3, "mlan");
    tileMap.put(4, "mquy");
    tileMap.put(5, "mphung");
    tileMap.put(6, "tlong");
    tileMap.put(7, "tlan");
    tileMap.put(8, "tquy");
    tileMap.put(9, "tphung");
  }
  public WheelItem() {
    incrementer = new AtomicInteger();
    incrementer.set(0);
  }

  @Override
  public void handle(RoutingContext ctx) {
    String megaID = Integer.toString(incrementer.getAndIncrement()%10000);//ctx.request().getParam("megaID");
    String  token  = ctx.request().getParam("token");

    TLS.redisApi.get(megaID, ar -> {
      if (ar.succeeded()) {
        String strData    = ar.result().toString(StandardCharsets.UTF_8);

        if (strData == null) {
          ctx.response().putHeader("Content-Type",  "text/json")
                  .end(Json.encode(Resp.ItemFail.of("failed", 0)));
        }

        User user         = Utilities.gson.fromJson(ar.result().toString(), User.class);

        if (user.turns > 0) {
          //todo blacklist
          Item randItem = calcRnd(ItemConfig.lsItem);
          updateHistory(user, randItem);
          updateInventory(user, randItem);
          user.turns--;

          String resKey = tileMap.get(randItem.type);
          TLS.redisApi.get(resKey, kar -> {
            if (kar.succeeded()) {
              if (kar.result().toInteger() < ItemConfig.lsItem.get(randItem.type).maximum) {
                TLS.redisApi.incr(resKey, incrar -> {});
                TLS.redisApi.set(Arrays.asList(megaID, Utilities.gson.toJson(user)), sar -> {});

                //log
                String userJson = Utilities.gson.toJson(user);
                String cat      = userJson + "getItem" + "NPW7S4EFSS";
                try {
                  TLS.client.get("http://68.183.180.71:3000/api/v1/log/add")
                          .addQueryParam("type", "getItem")
                          .addQueryParam("name", "tylinh01")
                          .addQueryParam("data", Utilities.gson.toJson(user))
                          .addQueryParam("hash", Utilities.md5Encode(cat)).send(logar -> {});
                }
                catch (Exception e) {
                  //todo
                }
              }
              else {
                ctx.response().putHeader("Content-Type", "text/json")
                        .end(Json.encode(Resp.ItemFail.of("failed", 0)));
              }
            }
            else {
              ctx.response().putHeader("Content-Type", "text/json")
                      .end(Json.encode(Resp.ItemFail.of("failed", 0)));
            }
          });

          ctx.response().putHeader("Content-Type", "text/json")
                  .end(Json.encode(Resp.ItemOk.of(randItem.type, randItem.name, user.turns)));
        }
        else {
          ctx.response().putHeader("Content-Type", "text/json")
                  .end(Json.encode(Resp.ItemFail.of("failed", 0)));
        }
      }
      else
        ctx.response().putHeader("Content-Type", "text/json")
                .end(Json.encode(Resp.ItemFail.of("failed", 0)));
    });
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

  private void updateHistory(User user, Item item) {
    if (user.histories == null || user.histories.size() == 0) {
      user.histories = new ArrayList<>();
      user.histories.add(History.of(item.type, item.name));
    }
    else {
      user.histories.add(History.of(item.type, item.name));
    }
  }

  private void updateInventory(User user, Item item) {
    if (user.inventories == null || user.inventories.size() == 0) {
      user.inventories = new ArrayList<>();
      user.inventories.add(Inventory.of(item.type, item.name, 1, item.is_gift, item.upgrade, item.condi_merge));
    }
    else {
      Inventory target = null;
      for (Inventory inv : user.inventories) {
        if (inv.type == item.type) {
          target = inv;
          break;
        }
      }
      if (target != null) {
        target.amount += 1;
      }
      else {
        user.inventories.add(Inventory.of(item.type, item.name, 1, item.is_gift, item.upgrade, item.condi_merge));
      }
    }
  }
}