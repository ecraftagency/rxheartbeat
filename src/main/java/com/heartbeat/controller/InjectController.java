package com.heartbeat.controller;

import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.service.SessionInjector;
import com.heartbeat.service.impl.EvilInjector;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class InjectController implements Handler<RoutingContext> {
  SessionInjector sessionInjector;

  public InjectController() {
    sessionInjector = new EvilInjector();
  }

  @Override
  public void handle(RoutingContext ctx) {
    ExtMessage resp     = ExtMessage.item();

    try {
      int id          = ctx.getBodyAsJson().getInteger("id");
      String path     = ctx.getBodyAsJson().getString("path");
      String value    = ctx.getBodyAsJson().getString("value");
      Session session = SessionPool.getSessionFromPool(id);

      if (session != null) {
        try {
          sessionInjector.inject(session, path, value);
          ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        }
        catch (Exception e) {
          resp.msg = e.getMessage();
          ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        }
      }
    }
    catch (Exception e) {
      ctx.response().setStatusCode(404).end();
    }
  }
}
