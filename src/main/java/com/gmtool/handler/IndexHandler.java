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

import static com.gmtool.GMTool.eventBus;
import static com.gmtool.GMTool.templateEngine;

public class IndexHandler implements Handler<RoutingContext> {
  public static List<NavEntry> navList = new ArrayList<>();
  static  {
    navList.add(NavEntry.ofActive("Server"));
    navList.add(NavEntry.ofDefault("User", "user"));
    navList.add(NavEntry.ofDefault("Event", "event"));
    navList.add(NavEntry.ofDefault("Config", "config"));
  }

  @Override
  public void handle(RoutingContext ctx) {
    JsonObject jsonMessage = new JsonObject().put("cmd", "getNodes");
    eventBus.request(Constant.SYSTEM_INFO.GATEWAY_EVT_BUS, jsonMessage, ar -> {
      if (ar.succeeded()) {
        JsonObject resp     = (JsonObject) ar.result().body();
        Type listOdNode     = new TypeToken<List<Node>>() {}.getType();
        List<Node> nodes    = Utilities.gson.fromJson(resp.getJsonArray("nodes").toString(), listOdNode);

        ctx.put("title", "lolol");
        ctx.put("nodes", nodes);
        ctx.put("navList", navList);
        ctx.put("activeNav", navList.get(0));

        templateEngine.render(ctx.data(), "webroot/html/navbar.ftl", nar -> {
          if (nar.succeeded()) {
            templateEngine.render(ctx.data(), "webroot/html/index.ftl", rar -> {
              if (rar.succeeded()) {
                ctx.response().putHeader("Content-Type", "text/html");
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