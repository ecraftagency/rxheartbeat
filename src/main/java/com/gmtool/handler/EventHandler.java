package com.gmtool.handler;

import com.common.Constant;
import com.common.Utilities;
import com.gmtool.model.NavEntry;
import com.google.gson.reflect.TypeToken;
import com.transport.model.Node;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.gmtool.GMTool.eventBus;
import static com.gmtool.GMTool.templateEngine;

public class EventHandler implements Handler<RoutingContext> {
  public static List<NavEntry> navList = new ArrayList<>();

  static  {
    navList.add(NavEntry.ofDefault("Server", "/"));
    navList.add(NavEntry.ofDefault("User", "user"));
    navList.add(NavEntry.ofDefault("Mail", "mail"));
    navList.add(NavEntry.ofDefault("Config", "config"));
    navList.add(NavEntry.ofActive("Event"));
  }

  @Override
  public void handle(RoutingContext ctx) {
    JsonObject jsonMessage = new JsonObject().put("cmd", "getNodes");
    eventBus.request(Constant.SYSTEM_INFO.GATEWAY_EVT_BUS, jsonMessage, ar -> {
      if (ar.succeeded()) {
        JsonObject resp     = (JsonObject) ar.result().body();
        Type listOdNode     = new TypeToken<List<Node>>() {}.getType();
        List<Node> nodes    = Utilities.gson.fromJson(resp.getJsonArray("nodes").toString(), listOdNode);

        ctx.put("ver", ThreadLocalRandom.current().nextInt(0,100000));
        ctx.put("navList", navList);
        ctx.put("activeNav", navList.get(4));
        ctx.put("nodes", nodes);

        templateEngine.render(ctx.data(), "webroot/html/navbar.ftl", nar -> {
          if (nar.succeeded()) {
            templateEngine.render(ctx.data(), "webroot/html/event.ftl", rar -> {
              if (rar.succeeded()) {
                ctx.response().putHeader("Content-Type", "text/html");
                ctx.response().putHeader("Cache-Control", "no-store");
                ctx.response().end(rar.result());
              } else {
                ctx.fail(rar.cause());
              }
            });
          }
          else {
            ctx.fail(nar.cause());
          }
        });
      }
    });
  }
}
