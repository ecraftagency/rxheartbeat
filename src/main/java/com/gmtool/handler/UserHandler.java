package com.gmtool.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.ThreadLocalRandom;

import static com.gmtool.GMTool.templateEngine;

public class UserHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    ctx.put("title", "User");
    ctx.put("ver", ThreadLocalRandom.current().nextInt(0,100000));
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
}