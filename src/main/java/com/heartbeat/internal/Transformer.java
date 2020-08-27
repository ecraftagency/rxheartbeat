package com.heartbeat.internal;

import com.common.Constant;
import com.common.LOG;
import com.heartbeat.event.ExtEventInfo;
import com.heartbeat.event.ExtIdolEventInfo;
import com.heartbeat.event.ExtRankingInfo;
import com.heartbeat.model.Session;
import com.heartbeat.model.data.UserLDB;
import com.statics.OfficeData;
import com.statics.PaymentData;
import com.statics.PropData;
import com.statics.VipData;
import com.transport.model.LDBObj;
import com.transport.model.MailObj;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.common.Constant.*;

@SuppressWarnings("unused")
public class Transformer {
  public static Map<Integer, String> userEvtId2Name;
  public static Map<Integer, String> rankEvtId2name;
  public static Map<Integer, String> idolEvtId2name;

  public static DateFormat formatter;

  static {
    formatter =  new SimpleDateFormat(Constant.DATE_PATTERN);
    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

    userEvtId2Name = new HashMap<>();
    userEvtId2Name.put(USER_EVENT.APT_BUFF_USE_EVT_ID,   "Sử dụng cuộn cường hóa");
    userEvtId2Name.put(USER_EVENT.VIEW_PROD_EVT_ID,      "Cày View");
    userEvtId2Name.put(USER_EVENT.VIEW_SPEND_EVT_ID,     "Tiêu hao view");
    userEvtId2Name.put(USER_EVENT.CRT_PROD_EVT_ID,       "Sáng tác");
    userEvtId2Name.put(USER_EVENT.FAN_PROD_EVT_ID,       "Fan metting");
    userEvtId2Name.put(USER_EVENT.FAN_SPEND_EVT_ID,      "Tiêu hao fan");
    userEvtId2Name.put(USER_EVENT.GAME_SHOW_EVT_ID,      "Phụ bản game show");
    userEvtId2Name.put(USER_EVENT.MONEY_SPEND_EVT_ID,    "Tiêu hao gole");
    userEvtId2Name.put(USER_EVENT.TIME_SPEND_EVT_ID,     "Tiêu hao time");
    userEvtId2Name.put(USER_EVENT.TOTAL_TALENT_EVT_ID,   "Tăng tổng tài năng");

    rankEvtId2name = new HashMap<>();
    rankEvtId2name.put(RANK_EVENT.TOTAL_TALENT_RANK_ID, "Top tăng tổng tài năng");
    rankEvtId2name.put(RANK_EVENT.FAN_SPEND_RANK_ID, "Top tiêu hao fan");
    rankEvtId2name.put(RANK_EVENT.MONEY_SPEND_RANK_ID, "Top tiêu hao money");
    rankEvtId2name.put(RANK_EVENT.VIEW_SPEND_RANK_ID, "Top tiêu hao view");
    rankEvtId2name.put(RANK_EVENT.FIGHT_RANK_ID, "Top đi ải");

    idolEvtId2name = new HashMap<>();
    idolEvtId2name.put(IDOL_EVENT.BP_EVT_ID, "Idol Event Black Pink");
    idolEvtId2name.put(IDOL_EVENT.DB_EVT_ID, "Idol Event BB");
  }

  //transform runtime object to view object
  public static JsonObject transformSession(Session session) {
    JsonObject gi         = new JsonObject();
    JsonArray items       = new JsonArray();
    JsonArray mails       = new JsonArray();
    JsonObject ss         = new JsonObject();

    gi.put("Tên",         session.userGameInfo.displayName);
    gi.put("Giới Tính",   session.userGameInfo.gender == 0 ? "Nam" : "Nữ");
    gi.put("EXP",         session.userGameInfo.exp);
    gi.put("Điểm VIP",    session.userGameInfo.vipExp);
    gi.put("Cấp VIP",     VipData.getVipData(session.userGameInfo.vipExp).level);
    gi.put("Hạng Sao",    OfficeData.officeLV.get(session.userGameInfo.titleId).name);
    gi.put("Money",       session.userGameInfo.money);
    gi.put("View",        session.userGameInfo.view);
    gi.put("Fan",         session.userGameInfo.fan);


    long time = session.userGameInfo.time;
    long nDay   = time/86400;
    time -= (nDay*86400);
    long nHour  = time/3600;
    time -= (nHour*3600);
    long nMin = time/60;
    time -= nMin*60;
    long nSec = time;
    gi.put("Time còn lại",        String.format("%d:%02d:%02d:%02d", nDay, nHour, nMin, nSec));

    Date start    = new Date(session.userProfile.banTo*1000L);
    String ban    = formatter.format(start);
    gi.put("Bị ban đến",  ban);

    for (Map.Entry<Integer, Integer> entry : session.userInventory.userItems.entrySet()) {
      PropData.Prop prop = PropData.propMap.get(entry.getKey());
      if (prop != null) {
        items.add(new JsonObject().put("id", prop.propID).put("name", prop.name).put("qty", entry.getValue()));
      }
    }

    if (session.userInbox.privateMails != null) {
      for (MailObj priMsg : session.userInbox.privateMails) {
        mails.add(new JsonObject().put("Title", priMsg.title).put("Content", priMsg.msg).put("Reward", priMsg.rewards));
      }
    }

    ss.put("userGameInfo", gi);
    ss.put("userInventory", items);
    ss.put("userInbox", mails);
    return ss;
  }

  public static JsonArray transformUserEvent() {
    JsonArray res = new JsonArray();

    for (ExtEventInfo ei : USER_EVENT.evtMap.values()){
      String  name        = userEvtId2Name.getOrDefault(ei.eventId, "");
      String  strStart;
      String  strEnd;

      try {
        if (ei.startTime <= 0 || ei.endTime <= 0)
          throw new IllegalArgumentException();

        Date start  = new Date(ei.startTime*1000L);
        strStart    = formatter.format(start);

        Date end    = new Date(ei.endTime*1000L);
        strEnd      = formatter.format(end);
      }
      catch (Exception e) {
        strEnd    = "";
        strStart  = "";
      }
      JsonObject evt =  new JsonObject();
      evt.put("eventId",    ei.eventId);
      evt.put("eventName",  name);
      evt.put("startDate",  strStart);
      evt.put("endDate",    strEnd);
      evt.put("flushDelay", ei.flushDelay);
      evt.put("active",     ei.active ? "Active" : "InActive");
      res.add(evt);
    }
    return res;
  }

  public static JsonArray transformIdolEvent() {
    JsonArray res = new JsonArray();

    for (ExtIdolEventInfo ei : IDOL_EVENT.evtMap.values()){
      String  name        = idolEvtId2name.getOrDefault(ei.eventId, "");
      String  strStart;
      String  strEnd;

      try {
        if (ei.startTime <= 0 || ei.endTime <= 0)
          throw new IllegalArgumentException();

        Date start  = new Date(ei.startTime*1000L);
        strStart    = formatter.format(start);

        Date end    = new Date(ei.endTime*1000L);
        strEnd      = formatter.format(end);
      }
      catch (Exception e) {
        strEnd    = "";
        strStart  = "";
      }
      JsonObject evt =  new JsonObject();
      evt.put("eventId",    ei.eventId);
      evt.put("eventName",  name);
      evt.put("startDate",  strStart);
      evt.put("endDate",    strEnd);
      evt.put("flushDelay", ei.flushDelay);
      evt.put("active",     ei.active ? "Active" : "InActive");
      res.add(evt);
    }
    return res;
  }

  public static JsonArray transformRankingEvent() {
    JsonArray res         = new JsonArray();

    for (ExtRankingInfo ri : RANK_EVENT.evtMap.values()){
      String  name        = rankEvtId2name.getOrDefault(ri.eventId, "");
      String  strStart;
      String  strEnd;

      try {
        if (ri.startTime <= 0 || ri.endTime <= 0)
          throw new IllegalArgumentException();

        Date start  = new Date(ri.startTime*1000L);
        strStart    = formatter.format(start);

        Date end    = new Date(ri.endTime*1000L);
        strEnd      = formatter.format(end);
      }
      catch (Exception e) {
        strEnd    = "";
        strStart  = "";
      }
      JsonObject evt =  new JsonObject();
      evt.put("eventId",    ri.eventId);
      evt.put("eventName",  name);
      evt.put("startDate",  strStart);
      evt.put("endDate",    strEnd);
      evt.put("flushDelay", ri.flushDelay);
      evt.put("active",     ri.active ? "Active" : "InActive");
      res.add(evt);
    }

    return res;
  }

  public static JsonArray transformConstant() {
    JsonArray res = new JsonArray();
    res.add(constField("Time tặng ban đầu", "USER_GAME_INFO.INIT_TIME_GIFT", "Giây", Integer.toString(USER_GAME_INFO.INIT_TIME_GIFT), "(0-n]"));
    res.add(constField("Level active time", "USER_GAME_INFO.TIME_ACTIVE_LEVEL", "Level", Integer.toString(USER_GAME_INFO.TIME_ACTIVE_LEVEL), "phải nằm trong các giá trị hạng sao [1,18]"));
    res.add(constField("Time tạo công ty", "GROUP.CREATE_GROUP_TIME_COST", "Giây", Integer.toString(GROUP.CREATE_GROUP_TIME_COST), "(0-n]"));
    res.add(constField("Thời gian lưu trên bảng thương hiệu", "TITLE.EXPIRY", "Phút", Integer.toString(TITLE.EXPIRY), "(0-n]"));
    res.add(constField("Exp tư chất tiêu hao khi up tư chất", "USER_IDOL.APT_EXP_COST_PER_UPGRADE", "Số nguyên", Integer.toString(USER_IDOL.APT_EXP_COST_PER_UPGRADE), "(0-n]"));
    res.add(constField("Exp tư chất ban đầu", "USER_IDOL.INIT_APT_EXP", "Số nguyên", Integer.toString(USER_IDOL.INIT_APT_EXP), "(0-n]"));

    res.add(constField("Money ban đầu", "USER_GAME_INFO.INIT_MONEY", "Số nguyên", Integer.toString(USER_GAME_INFO.INIT_MONEY), "(0-n]"));
    res.add(constField("View ban đầu", "USER_GAME_INFO.INIT_VIEW", "Số nguyên", Integer.toString(USER_GAME_INFO.INIT_VIEW), "(0-n]"));
    res.add(constField("Fan ban đầu", "USER_GAME_INFO.INIT_FAN", "Số nguyên", Integer.toString(USER_GAME_INFO.INIT_FAN), "(0-n]"));

    res.add(constField("Level active time","UNLOCK_FUNCTION.TIME_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.TIME_UNLOCK_LEVEL), "[1,18]"));
    res.add(constField("Level active game show", "UNLOCK_FUNCTION.GAME_SHOW_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.GAME_SHOW_UNLOCK_LEVEL), "[1,18]"));
    res.add(constField("Level active shop(cửa tiệm)", "UNLOCK_FUNCTION.SHOP_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.SHOP_UNLOCK_LEVEL), "[1,18]"));
    res.add(constField("Level active đi đu đưa", "UNLOCK_FUNCTION.TRAVEL_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.TRAVEL_UNLOCK_LEVEL), "[1,18]"));
    res.add(constField("Level active công ty","UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL), "[1,18]"));
    res.add(constField("Level active QR code friend", "UNLOCK_FUNCTION.FRIEND_QR_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.FRIEND_QR_UNLOCK_LEVEL), "[1,18]"));
    res.add(constField("Level active đi shopping", "UNLOCK_FUNCTION.SHOPPING_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.SHOPPING_UNLOCK_LEVEL), "[1,18]"));
    res.add(constField("Level active skip ải", "UNLOCK_FUNCTION.SKIP_FIGHT_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.SKIP_FIGHT_UNLOCK_LEVEL), "[1,18]"));
    res.add(constField("Level active shopping nhanh", "UNLOCK_FUNCTION.FAST_SHOPPING_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.FAST_SHOPPING_UNLOCK_LEVEL), "[1,18]"));
    res.add(constField("Level active chạy show", "UNLOCK_FUNCTION.RUN_SHOW_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.RUN_SHOW_UNLOCK_LEVEL), "[1,18]"));
    res.add(constField("Level active chạy show nhanh", "UNLOCK_FUNCTION.FAST_RUN_SHOW_UNLOCK_LEVEL", "level", Integer.toString(UNLOCK_FUNCTION.FAST_RUN_SHOW_UNLOCK_LEVEL), "[1,18]"));

    return res;
  }

  private static JsonObject constField(String fieldName, String editPath, String unit, String value, String note) {
    JsonObject field = new JsonObject();
    field.put("fieldName", fieldName);
    field.put("editPath", editPath);
    field.put("unit", unit);
    field.put("value", value);
    field.put("note", note);
    return field;
  }

  public static void transformLDB(int ldbId, Handler<AsyncResult<JsonArray>> ar) {
    UserLDB.getLDB(ldbId, lar -> {
      if (lar.succeeded()) {
        JsonArray res = new JsonArray();
        List<LDBObj> ldb = lar.result();
        ldb.sort((a,b) -> {
          if (a.score > b.score)
            return -1;
          else if (a.score < b.score)
            return 1;
          return 0;
        });
        int idx = 0;
        for (LDBObj ldbObj : ldb) {
          JsonObject record = new JsonObject();
          record.put("rank", ++idx).put("id", ldbObj.id).put("name", ldbObj.displayName).put("score", ldbObj.score);
          res.add(record);
        }
        ar.handle(Future.succeededFuture(res));
      }
    });
  }

  public static JsonArray transformPaymentData() {
    JsonArray res = new JsonArray();
    if (PaymentData.paymentDtoMap == null || PaymentData.paymentDtoMap.size() == 0) {
      LOG.globalException("node", "transformPaymentData", "invalid payment data");
      return res;
    }

    for (PaymentData.PaymentDto dto : PaymentData.paymentDtoMap.values()) {
      res.add(new JsonObject().put("id", dto.id).put("Giá web", dto.webVal).put("Giá iap", dto.iapVal)
              .put("vip", dto.vip).put("time", dto.time).put("items", dto.reward));
    }

    return res;
  }
}