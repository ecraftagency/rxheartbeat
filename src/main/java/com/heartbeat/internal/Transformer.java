package com.heartbeat.internal;

import com.common.Constant;
import com.heartbeat.event.ExtEventInfo;
import com.heartbeat.model.Session;
import com.statics.OfficeData;
import com.statics.PropData;
import com.statics.VipData;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class Transformer {
  public static Map<Integer, String> id2Name;

  static {
    id2Name = new HashMap<>();
    id2Name.put(Constant.USER_EVENT.APT_BUFF_USE_EVT_ID,   "Sử dụng cuộn cường hóa");
    id2Name.put(Constant.USER_EVENT.VIEW_PROD_EVT_ID,      "Cày View");
    id2Name.put(Constant.USER_EVENT.VIEW_SPEND_EVT_ID,     "Tiêu hao view");
    id2Name.put(Constant.USER_EVENT.CRT_PROD_EVT_ID,       "Sáng tác");
    id2Name.put(Constant.USER_EVENT.FAN_PROD_EVT_ID,       "Fan metting");
    id2Name.put(Constant.USER_EVENT.FAN_SPEND_EVT_ID,      "Tiêu hao fan");
    id2Name.put(Constant.USER_EVENT.GAME_SHOW_EVT_ID,      "Phụ bản game ");
    id2Name.put(Constant.USER_EVENT.MONEY_SPEND_EVT_ID,    "Tiêu hao ");
    id2Name.put(Constant.USER_EVENT.TIME_SPEND_EVT_ID,     "Tiêu hao time");
    id2Name.put(Constant.USER_EVENT.TOTAL_TALENT_EVT_ID,   "Tăng tổng tài năng");
  }

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

  public static JsonArray transformUserEvent() {
    JsonArray res = new JsonArray();
    SimpleDateFormat sdf  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

    for (ExtEventInfo ei : Constant.USER_EVENT.evtMap.values()){
      String  name        = id2Name.getOrDefault(ei.eventId, "");
      String  strStart;
      String  strEnd;

      try {
        Date start  = new Date(System.currentTimeMillis());
        strStart    = sdf.format(start);
        Date end    = new Date(ei.endTime*1000);
        strEnd      = sdf.format(end);
      }
      catch (Exception e) {
        strEnd    = "";
        strStart  = "";
      }
      JsonObject evt =  new JsonObject();
      evt.put("eventId",    ei.eventId);
      evt.put("eventName",  name);
      evt.put("startDate",  strStart);
      evt.put("endDate",     strEnd);
      evt.put("active",     ei.active ? "Active" : "InActive");
      res.add(evt);
    }
    return res;
  }
}