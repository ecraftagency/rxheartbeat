package com.gmtool.handler;

import com.gmtool.NodeCache;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import static com.gmtool.GMTool.templateEngine;

public class EventHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    ctx.put("nodes", NodeCache.inst().getNodes());

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
}
