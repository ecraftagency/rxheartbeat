package com.gmtool.controller;

import com.common.Constant;
import com.common.Utilities;
import com.gmtool.Common;
import com.google.gson.reflect.TypeToken;
import com.heartbeat.model.Session;
import com.statics.OfficeData;
import com.statics.PropData;
import com.statics.VipData;
import com.transport.model.Node;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
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

  public void getUserInfo(RoutingContext ctx) {
    String strId  = ctx.getBodyAsString();
    int sessionId = Integer.parseInt(strId);
    JsonObject user = new JsonObject();

    Common.findNode(sessionId, nr -> {
      if (nr.succeeded()) {
        Common.getSession(sessionId, nr.result(), sr -> {
          if (sr.succeeded()) {
            Session session = sr.result();
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
            user.put("userGameInfo", gi);
            user.put("userInventory", it);
            user.put("msg", "ok");
            ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(user));
          }
          else {
            user.put("msg", sr.cause().getMessage());
            ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(user));
          }
        });
      }
      else {
        user.put("msg", nr.cause().getMessage());
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(user));
      }
    });
  }
}