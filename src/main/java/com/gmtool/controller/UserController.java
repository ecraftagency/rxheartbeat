package com.gmtool.controller;

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
    Session session = Session.of(1000000);
    session.initRegister("lahlahlah");

    JsonObject resp = new JsonObject();
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

    ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
  }
}