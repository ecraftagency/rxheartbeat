package com.gmtool.controller;

import com.gmtool.Common;
import com.heartbeat.model.Session;
import com.statics.OfficeData;
import com.statics.PropData;
import com.statics.VipData;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

public class UserController implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    String cmd = ctx.getBodyAsJson().getString("cmd");

    JsonObject resp = new JsonObject();
    try {
      switch (cmd) {
        case "inject":
          injectUser(ctx);
          return;
        case "getUserInfo":
          getUserInfo(ctx);
          return;
        default:
          resp.put("msg", "unknown_cmd");
          break;
      }
    }
    catch (Exception e) {
      resp.put("msg", e.getMessage());
    }

    ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
  }

  private void injectUser(RoutingContext ctx) {
    String strId        = ctx.getBodyAsJson().getString("sessionId");
    int sessionId       = Integer.parseInt(strId);
    JsonObject resp     = new JsonObject();

    Common.findNode(sessionId, nodeResult -> {
      if (nodeResult.succeeded()) {
        Common.injectSession(nodeResult.result(), ctx, sessionResult -> {
          if (sessionResult.succeeded()) {
            Session session = sessionResult.result();
            transformSessionData(resp, session);
            resp.put("msg", "ok");
          }
          else {
            resp.put("msg", sessionResult.cause().getMessage());
          }
          ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        });
      }
      else {
        resp.put("msg", nodeResult.cause().getMessage());
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
    });
  }

  public void getUserInfo(RoutingContext ctx) {
    String strId    = ctx.getBodyAsJson().getString("sessionId");
    int sessionId   = Integer.parseInt(strId);
    JsonObject resp = new JsonObject();

    Common.findNode(sessionId, nodeResult -> {
      if (nodeResult.succeeded()) {
        Common.getSession(sessionId, nodeResult.result(), sessionResult -> {
          if (sessionResult.succeeded()) {
            Session session = sessionResult.result();
            transformSessionData(resp, session);
            resp.put("msg", "ok");
            ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
          }
          else {
            resp.put("msg", sessionResult.cause().getMessage());
          }
          ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        });
      }
      else {
        resp.put("msg", nodeResult.cause().getMessage());
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
    });
  }

  private void transformSessionData(JsonObject resp, Session session) {
    JsonObject gi = new JsonObject();
    JsonObject it = new JsonObject();

    gi.put("Tên",         session.userGameInfo.displayName);
    gi.put("Giới Tính",   session.userGameInfo.gender == 0 ? "Nam" : "Nữ");
    gi.put("EXP",         session.userGameInfo.exp);
    gi.put("Điểm VIP",    session.userGameInfo.vipExp);
    gi.put("Cấp VIP",     VipData.getVipData(session.userGameInfo.vipExp).level);
    gi.put("Hạng Sao",    OfficeData.officeLV.get(session.userGameInfo.titleId).name);
    gi.put("Money",       session.userGameInfo.money);
    gi.put("View",        session.userGameInfo.view);
    gi.put("Fan",         session.userGameInfo.fan);

    for (Map.Entry<Integer, Integer> entry : session.userInventory.userItems.entrySet()) {
      PropData.Prop prop = PropData.propMap.get(entry.getKey());
      if (prop != null) {
        it.put(prop.name, entry.getValue());
      }
    }
    resp.put("userGameInfo", gi);
    resp.put("userInventory", it);
  }
}