package com.gmtool;

import com.common.Constant;
import com.common.Utilities;
import com.google.gson.reflect.TypeToken;
import com.transport.model.Node;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static com.gmtool.GMTool.eventBus;
import static com.gmtool.GMTool.templateEngine;

public class Renderer implements Handler<RoutingContext> {
  //public static String host = "http://localhost:3000";
  public static String host = "http://18.141.216.52:3000";

  @Override
  public void handle(RoutingContext ctx) {
    String path = ctx.request().getParam("path");
    ctx.put("host", host);
    switch (path) {
      case "server":
        renderIndex(ctx);
        return;
      case "user":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/user.ftl", ctx);
        return;
      case "mail":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/mail.ftl", ctx);
        return;
      case "config":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/config.ftl", ctx);
        return;
      case "ldb":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/leaderboard.ftl", ctx);
        return;
      case "event":
        ctx.put("nodes", GMTool.getNodes());
        ctx.put("evtType", Arrays.asList("userEvents", "idolEvents", "rankEvents")); //todo double check template/controller
        render("webroot/html/event.ftl", ctx);
        return;
      default:
        ctx.response().setStatusCode(404).end();
    }
  }

  private void render(String templatePath, RoutingContext ctx) {
      templateEngine.render(ctx.data(), templatePath, rar -> {
        if (rar.succeeded()) {
          ctx.response().putHeader("Content-Type", "text/html");
          ctx.response().end(rar.result());
        } else {
          ctx.fail(rar.cause());
        }
      });
  }

  private void renderIndex(RoutingContext ctx) {
    JsonObject jsonMessage = new JsonObject().put("cmd", "getNodes");
    eventBus.request(Constant.SYSTEM_INFO.GATEWAY_EVT_BUS, jsonMessage, ar -> {
      if (ar.succeeded()) {
        JsonObject resp     = (JsonObject) ar.result().body();
        Type listOdNode     = new TypeToken<List<Node>>() {}.getType();
        List<Node> nodes    = Utilities.gson.fromJson(resp.getJsonArray("nodes").toString(), listOdNode);
        ctx.put("nodes", nodes);
        GMTool.setNodes(nodes);
        render("webroot/html/index.ftl", ctx);
      }
    });
  }
}