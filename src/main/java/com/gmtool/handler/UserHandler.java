package com.gmtool.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.gmtool.GMTool.templateEngine;

public class UserHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
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