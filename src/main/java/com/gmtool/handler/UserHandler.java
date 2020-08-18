package com.gmtool.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import static com.gmtool.GMTool.templateEngine;

public class UserHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    ctx.put("title", "User");
    templateEngine.render(ctx.data(), "webroot/html/user.ftl", rar -> {
      if (rar.succeeded()) {
        ctx.response().putHeader("Content-Type", "text/html");
        ctx.response().end(rar.result());
      } else {
        ctx.fail(rar.cause());
      }
    });
  }
}