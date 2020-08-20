package com.gmtool.handler;

import com.gmtool.model.NavEntry;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.gmtool.GMTool.templateEngine;

public class UserHandler implements Handler<RoutingContext> {
  public static List<NavEntry> navList = new ArrayList<>();
  static  {
    navList.add(NavEntry.ofDefault("Server", "/"));
    navList.add(NavEntry.ofActive("User"));
    navList.add(NavEntry.ofDefault("Event", "event"));
    navList.add(NavEntry.ofDefault("Config", "config"));
  }
  @Override
  public void handle(RoutingContext ctx) {
    ctx.put("title", "User");
    ctx.put("ver", ThreadLocalRandom.current().nextInt(0,100000));
    ctx.put("navList", navList);
    ctx.put("activeNav", navList.get(1));
    templateEngine.render(ctx.data(), "webroot/html/navbar.ftl", nar -> {
      if (nar.succeeded()) {
        templateEngine.render(ctx.data(), "webroot/html/user.ftl", rar -> {
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
}