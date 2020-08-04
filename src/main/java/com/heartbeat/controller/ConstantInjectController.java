package com.heartbeat.controller;

import com.heartbeat.service.ConstantInjector;
import com.heartbeat.service.impl.MaydayInjector;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class ConstantInjectController implements Handler<RoutingContext> {
  ConstantInjector constantInjector;

  public ConstantInjectController() {
    constantInjector = new MaydayInjector();
  }

  @Override
  public void handle(RoutingContext ctx) {
    ExtMessage resp     = ExtMessage.item();

    try {
      String value      = ctx.getBodyAsJson().getString("value");
      try {
        constantInjector.inject("", value);
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
      catch (Exception e) {
        resp.msg = e.getMessage();
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
    }
    catch (Exception e) {
      ctx.response().setStatusCode(404).end();
    }
  }
}