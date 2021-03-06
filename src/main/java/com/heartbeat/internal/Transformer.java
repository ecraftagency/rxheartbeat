package com.heartbeat.internal;

import com.common.Constant;
import com.common.LOG;
import com.heartbeat.event.IdolEvent;
import com.heartbeat.event.RankingEvent;
import com.heartbeat.event.TimingEvent;
import com.heartbeat.model.Session;
import com.heartbeat.model.data.UserLDB;
import com.heartbeat.scheduler.ExtendEventInfo;
import com.statics.*;
import com.transport.model.LDBObj;
import com.transport.model.MailObj;
import com.transport.model.PaymentTransaction;
import com.transport.model.RollCall;
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
  public static Map<Integer, String> groupEvtId2name;

  public static DateFormat formatter;

  static {
    formatter =  new SimpleDateFormat(Constant.DATE_PATTERN);
    formatter.setTimeZone(TimeZone.getTimeZone(Constant.TIME_ZONE));

    userEvtId2Name = new HashMap<>();
    userEvtId2Name.put(TimingEvent.APT_BUFF_USE_EVT_ID,    "Sử dụng cuộn cường hóa");
    userEvtId2Name.put(TimingEvent.VIEW_PROD_EVT_ID,       "Cày View");
    userEvtId2Name.put(TimingEvent.VIEW_SPEND_EVT_ID,      "Tiêu hao view");
    userEvtId2Name.put(TimingEvent.CRT_PROD_EVT_ID,        "Sáng tác");
    userEvtId2Name.put(TimingEvent.FAN_PROD_EVT_ID,        "Fan metting");
    userEvtId2Name.put(TimingEvent.FAN_SPEND_EVT_ID,       "Tiêu hao fan");
    userEvtId2Name.put(TimingEvent.GAME_SHOW_EVT_ID,       "Phụ bản game show");
    userEvtId2Name.put(TimingEvent.MONEY_SPEND_EVT_ID,     "Tiêu hao gole");
    userEvtId2Name.put(TimingEvent.TIME_SPEND_EVT_ID,      "Tiêu hao time");
    userEvtId2Name.put(TimingEvent.TOTAL_TALENT_EVT_ID,    "Tăng tổng tài năng");
    userEvtId2Name.put(TimingEvent.VIP_INCR_EVT_ID,        "Thưởng Nạp");

    rankEvtId2name = new HashMap<>();
    rankEvtId2name.put(RankingEvent.TOTAL_TALENT_RANK_ID,   "Top tăng tổng tài năng");
    rankEvtId2name.put(RankingEvent.FAN_SPEND_RANK_ID,      "Top tiêu hao fan");
    rankEvtId2name.put(RankingEvent.MONEY_SPEND_RANK_ID,    "Top tiêu hao money");
    rankEvtId2name.put(RankingEvent.VIEW_SPEND_RANK_ID,     "Top tiêu hao view");
    rankEvtId2name.put(RankingEvent.FIGHT_RANK_ID,          "Top đi ải");

    idolEvtId2name = new HashMap<>();
    idolEvtId2name.put(IdolEvent.BP_EVT_ID, "Idol Event Black Pink");
    idolEvtId2name.put(IdolEvent.DB_EVT_ID, "Idol Event BB");
    idolEvtId2name.put(IdolEvent.NT_EVT_ID, "Idol Event Ngoc Trinh");

    groupEvtId2name = new HashMap<>();
    groupEvtId2name.put(GROUP_EVENT.GE_PROD_EVT_ID, "Nhiệm vụ sáng tác");
    groupEvtId2name.put(GROUP_EVENT.GE_GS_EVT_ID, "Nhiệm vụ đi phụ bản");
    groupEvtId2name.put(GROUP_EVENT.GE_CRZ_DEGREE_EVT_ID, "Nhiệm vụ đạt độ sôi nổi");
    groupEvtId2name.put(GROUP_EVENT.GE_MONTHLY_GC_EVT_ID, "Nhiệm vụ thẻ tháng");
  }

  public static JsonObject transformSession(Session session) {
    JsonObject gi         = new JsonObject();
    JsonArray items       = new JsonArray();
    JsonArray mails       = new JsonArray();
    JsonObject ss         = new JsonObject();
    JsonArray payment     = new JsonArray();
    JsonArray userGift    = new JsonArray();

    gi.put("Tên",         session.userGameInfo.displayName);
    gi.put("Giới Tính",   session.userGameInfo.gender == 0 ? "Nam" : "Nữ");
    gi.put("EXP",         session.userGameInfo.exp);
    gi.put("Điểm VIP",    session.userGameInfo.vipExp);
    gi.put("Cấp VIP",     VipData.getVipData(session.userGameInfo.vipExp).level);
    gi.put("Hạng Sao",    OfficeData.officeLV.get(session.userGameInfo.titleId).name);
    gi.put("Money",       session.userGameInfo.money);
    gi.put("View",        session.userGameInfo.view);
    gi.put("Fan",         session.userGameInfo.fan);
    gi.put("NetPoint",    session.userGameInfo.netPoint);


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

    session.userInventory.reBalance();
    for (Map.Entry<Integer, Integer> entry : session.userInventory.userItems.entrySet()) {
      PropData.Prop prop = PropData.propMap.get(entry.getKey());
      if (prop != null) {
        items.add(new JsonObject().put("id", prop.propID).put("name", prop.name).put("qty", entry.getValue()));
      }
    }

//    for (Map.Entry<Integer, List<Integer>> entry : session.userInventory.expItems.entrySet()) {
//      PropData.Prop prop = PropData.propMap.get(entry.getKey());
//      if (prop != null) {
//        List<Integer> itemVector = entry.getValue();
//        if (itemVector != null && itemVector.size() > 0)
//          items.add(new JsonObject().put("id", prop.propID).put("name", prop.name).put("qty", itemVector.size()));
//      }
//    }

    if (session.userInbox.privateMails != null) {
      for (MailObj priMsg : session.userInbox.privateMails) {
        mails.add(new JsonObject().put("Title", priMsg.title).put("Content", priMsg.msg).put("Reward", priMsg.rewards));
      }
    }

    if (session.userPayment != null) {
      for (PaymentTransaction trans : session.userPayment.history) {
        Date payAt    = new Date(trans.payAt*1000L);
        String strPayAt    = formatter.format(payAt);
        payment.add(new JsonObject()
                .put("transID", trans.transID)
                .put("Thanh toán lúc", strPayAt)
                .put("amount", trans.amount)
                .put("price", trans.price)
                .put("Gói Nạp", trans.itemId));
      }
    }


    session.userRollCall.reCalcGiftCardInfo(session, (int)(System.currentTimeMillis()/1000));
    for (RollCall.GiftInfo giftInfo : session.userRollCall.giftCards.values()) {
      Date pd = new Date(giftInfo.boughtTime*1000L);
      Date lc = new Date(giftInfo.lastClaimTime*1000L);
      String paidDate = formatter.format(pd);
      String lastClaim = formatter.format(lc);
      userGift.add(new JsonObject()
      .put("giftType", giftInfo.giftType)
      .put("Thanh toán lúc", paidDate)
      .put("Lần cuối nhận thẻ", lastClaim)
      .put("Số ngày còn hiệu lực", giftInfo.remainDay));
    }

    ss.put("userGameInfo", gi);
    ss.put("userInventory", items);
    ss.put("userInbox", mails);
    ss.put("userPayment", payment);
    ss.put("userGift", userGift);

    return ss;
  }

  public static JsonArray transformEvents(Map<Integer, List<ExtendEventInfo>> evtPlan, Map<Integer, String> nameMap) {
    JsonArray res = new JsonArray();
    int curSec = (int)(System.currentTimeMillis()/1000);

    for (List<ExtendEventInfo> subPlan : evtPlan.values()) {
      for (ExtendEventInfo ei : subPlan){
        String  name        = nameMap.getOrDefault(ei.eventId, "");
        String  strStart;
        String  strEnd;

        try {
          if (ei.startTime <= 0 || ei.endTime <= 0)
            throw new IllegalArgumentException();
          if (curSec - (ei.endTime + ei.flushDelay) > 60)
            continue;
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
        evt.put("rewardPack", ei.rewardPack);

        res.add(evt);
      }
    }

    return res;
  }

  public static JsonArray transformEvent(Map<Integer, ExtendEventInfo> evtMap, Map<Integer, String> nameMap) {
    JsonArray res = new JsonArray();

    for (ExtendEventInfo ei : evtMap.values()){
      String  name        = nameMap.getOrDefault(ei.eventId, "");
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
      evt.put("rewardPack", ei.rewardPack);

      res.add(evt);
    }
    return res;
  }

  public static JsonArray transformConstant() {
    JsonArray res = new JsonArray();
    res.add(constField("Time tặng ban đầu", "USER_GAME_INFO.INIT_TIME_GIFT", "Giây", Integer.toString(USER_GAME_INFO.INIT_TIME_GIFT), "(0-n]"));
    res.add(constField("Level active time", "USER_GAME_INFO.TIME_ACTIVE_LEVEL", "Level", Integer.toString(USER_GAME_INFO.TIME_ACTIVE_LEVEL), "phải nằm trong các giá trị hạng sao [1,18]"));
    res.add(constField("Time tạo công ty", "USER_GROUP.CREATE_GROUP_TIME_COST", "Giây", Integer.toString(USER_GROUP.CREATE_GROUP_TIME_COST), "(0-n]"));
    res.add(constField("Thời gian lưu trên bảng thương hiệu", "NET_AWARD.EXPIRY", "Phút", Integer.toString(NET_AWARD.EXPIRY), "(0-n]"));
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
    res.add(constField("Môi trường passport Phoenix", "PASSPORT.setEnv(boolean prod)", "true|false", Boolean.toString(PASSPORT.PROD), "[true, false]"));
    res.add(constField("Server version", "GAME_INFO.SERVER_VERSION", "Số nguyên", Integer.toString(GAME_INFO.SERVER_VERSION), "[140, ]"));
    res.add(constField("Min playable client version", "GAME_INFO.MIN_AVAILABLE_VERSION", "Số nguyên", Integer.toString(GAME_INFO.MIN_AVAILABLE_VERSION), "[140, ]"));
    res.add(constField("Google Play", "GAME_INFO.CH_PLAY_APP_LINK", "Chuỗi", GAME_INFO.CH_PLAY_APP_LINK, ""));
    res.add(constField("App Store", "GAME_INFO.APPLE_STORE_APP_LINK", "Chuỗi", GAME_INFO.APPLE_STORE_APP_LINK, ""));
    res.add(constField("Tính năng gift code", "GAME_FUNCTIONS.GIFT_CODE", "true|false", Boolean.toString(GAME_FUNCTIONS.GIFT_CODE), "[true|false]"));
    res.add(constField("Tính năng chat netalo", "GAME_FUNCTIONS.NETA_CHAT", "true|false", Boolean.toString(GAME_FUNCTIONS.NETA_CHAT), "[true|false]"));
    res.add(constField("Admin email", "GAME_INFO.ADMIN_MAIL", "Chuỗi", GAME_INFO.ADMIN_MAIL, "Đừng để = rỗng hoặc null"));
    res.add(constField("Link chia sẻ MGT", "GAME_INFO.REF_CODE_SHARE_LINK", "Chuỗi", GAME_INFO.REF_CODE_SHARE_LINK, "Đừng để = rỗng hoặc null"));
    res.add(constField("Fan Page", "GAME_INFO.FAN_PAGE", "Chuỗi", GAME_INFO.FAN_PAGE, "Đừng để = rỗng hoặc null"));
    res.add(constField("Bật tắt tính năng MGT", "GAME_FUNCTIONS.REF_CODE", "true|false", Boolean.toString(GAME_FUNCTIONS.REF_CODE), "[true|false]"));

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

  public static JsonArray transformShopData() {
    JsonArray res = new JsonArray();
    if (ShopData.shopDtoMap == null || ShopData.shopDtoMap.size() == 0) {
      LOG.globalException("node", "transformPaymentData", "invalid payment data");
      return res;
    }

    for (ShopData.ShopDto dto : ShopData.shopDtoMap.values()) {

      res.add(new JsonObject().put("id", dto.id).put("type", dto.type).put("desc", dto.desc)
              .put("vipCond", dto.vipCond).put("timeCost", dto.timeCost)
              .put("dailyLimit", dto.dailyLimit).put("items", dto.format).put("status", dto.status));
    }

    return res;
  }
}