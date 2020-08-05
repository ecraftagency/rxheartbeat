package com.heartbeat.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heartbeat.common.Constant;
import com.heartbeat.service.ConstantInjector;
import com.heartbeat.service.impl.MaydayInjector;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class ConstantInjectController implements Handler<RoutingContext> {
  ConstantInjector constantInjector;
  Gson gson;
  public ConstantInjectController() {
    constantInjector = new MaydayInjector();
    GsonBuilder gsonBuilder  = new GsonBuilder();
    gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
    gson = gsonBuilder.create();
  }

  @Override
  public void handle(RoutingContext ctx) {
    ExtMessage resp     = ExtMessage.item();
    String cmd          = ctx.getBodyAsJson().getString("cmd");
    switch (cmd) {
      case "info":
        processInfo(ctx);
        return;
      case "inject":
        processInject(resp, ctx);
        return;
      default:
        ctx.response().setStatusCode(400).end("unknown_cmd");
    }
  }

  private void processInfo(RoutingContext ctx) {
    String value        = ctx.getBodyAsJson().getString("value");
    switch (value) {
      case "event":
        ctx.response().putHeader("Content-Type", "text/json").end(gson.toJson(new Constant.EVENT()));
        return;
      case "group":
        ctx.response().putHeader("Content-Type", "text/json").end(gson.toJson(new Constant.GROUP()));
        return;
      default:
        ctx.response().setStatusCode(400).end("unknown_value");
    }
  }

  private void processInject(ExtMessage resp, RoutingContext ctx) {
    try {
      String value      = ctx.getBodyAsJson().getString("value");
      try {
        constantInjector.inject("", value);
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
      catch (Exception e) {
        resp.msg = e.getMessage();
        ctx.response().setStatusCode(400).end(Json.encode(e));
      }
    }
    catch (Exception e) {
      ctx.response().setStatusCode(400).end(Json.encode(e));
    }
  }
}