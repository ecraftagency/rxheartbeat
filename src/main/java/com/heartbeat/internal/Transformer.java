package com.heartbeat.internal;

import com.heartbeat.model.Session;
import com.statics.OfficeData;
import com.statics.PropData;
import com.statics.VipData;
import io.vertx.core.json.JsonObject;

import java.util.Map;

public class Transformer {
  //transform runtime object to view object
  public static JsonObject transformSession(Session session) {
    JsonObject gi = new JsonObject();
    JsonObject it = new JsonObject();
    JsonObject ss = new JsonObject();

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

    ss.put("userGameInfo", gi);
    ss.put("userInventory", it);
    return ss;
  }
}