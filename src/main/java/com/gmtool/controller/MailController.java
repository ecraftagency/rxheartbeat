package com.gmtool.controller;

import com.common.Constant;
import com.common.Utilities;
import com.gmtool.Communicate;
import com.google.gson.reflect.TypeToken;
import com.transport.model.Node;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class MailController implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    String cmd = ctx.getBodyAsJson().getString("cmd");

    JsonObject resp = new JsonObject();
    try {
      switch (cmd) {
        case "sendMail":
          processSendMail(ctx);
          return;
        default:
          resp.put("msg", "unknown_cmd");
          break;
      }
    } catch (Exception e) {
      resp.put("msg", e.getMessage());
    }

    ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
  }

  private void processSendMail(RoutingContext ctx) {
    String nodeId   = ctx.getBodyAsJson().getString("serverId");
    JsonObject resp = new JsonObject();

    Communicate.findNodeById(Integer.parseInt(nodeId), nodeResult -> {
      if (nodeResult.succeeded()) {
        Communicate.sendMail(nodeResult.result(), ctx, sr -> {
          if (sr.succeeded()) {
            resp.put("msg", sr.result());
            ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
          }
          else {
            resp.put("msg", sr.cause().getMessage());
            ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
          }
        });
      }
      else {
        resp.put("msg", nodeResult.cause().getMessage());
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
    });
  }
}
