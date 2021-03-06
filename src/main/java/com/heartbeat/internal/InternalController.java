package com.heartbeat.internal;

import com.common.GlobalVariable;
import com.common.LOG;
import com.common.Utilities;
import com.gateway.model.Payload;
import com.google.gson.reflect.TypeToken;
import com.heartbeat.DailyStats;
import com.heartbeat.HBServer;
import com.heartbeat.db.cb.AbstractCruder;
import com.heartbeat.db.cb.CBMapper;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.db.dao.DailyStatsDAO;
import com.heartbeat.event.Common;
import com.heartbeat.event.IdolEvent;
import com.heartbeat.event.RankingEvent;
import com.heartbeat.event.TimingEvent;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserInbox;
import com.heartbeat.model.data.UserInventory;
import com.heartbeat.model.data.UserLDB;
import com.heartbeat.model.data.UserPayment;
import com.heartbeat.netaChat.NetaAPI;
import com.heartbeat.scheduler.ExtendEventInfo;
import com.heartbeat.service.ConstantInjector;
import com.heartbeat.service.SessionInjector;
import com.heartbeat.service.impl.EvilInjector;
import com.heartbeat.service.impl.MaydayInjector;
import com.statics.PaymentData;
import com.statics.ShopData;
import com.transport.IntMessage;
import com.transport.NetaGroup;
import com.transport.model.MailObj;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.lang.reflect.Type;
import java.util.*;
import static com.common.Constant.*;
import static com.common.Constant.PAYMENT.*;

public class InternalController implements Handler<Message<JsonObject>> {
  SessionInjector sessionInjector;
  ConstantInjector constantInjector;
  AbstractCruder<DailyStatsDAO> dailyStatsCB;
  public InternalController() {
    sessionInjector   = new EvilInjector();
    constantInjector  = new MaydayInjector();
    dailyStatsCB      = new AbstractCruder<>(DailyStatsDAO.class, HBServer.rxStatsBucket);
  }

  @Override
  public void handle(Message<JsonObject> ctx) {
    JsonObject resp = new JsonObject();
    String cmd      = "";
    try {
      JsonObject json = ctx.body();
      cmd             = json.getString("cmd");
      switch (cmd) {
        case "injectSession":
          processInjectSession(ctx);
          return;
        case "genWebPaymentLink":
          processGenWebPaymentLink(ctx);
          return;
        case "genIAPPaymentLink":
          processGenIAPPaymentLink(ctx);
          return;
        case "genNCExchangeLink":
          processGenNCExchangeLink(ctx);
          return;
        case "genNPExchangeLink":
          processGenNPExchangeLink(ctx);
          return;
        case "genGetRoleLink":
          processGenGetRoleLink(ctx);
          return;
        case "getSession":
          processGetSession(ctx);
          return;
        case "sendMail":
          processSendMail(ctx);
          return;
        case "sendPrivateMail":
          processSendPrivateMail(ctx);
          return;
        case "getEvents":
          processGetEvents(ctx);
          return;
        case "setUserEventTime":
          processSetUserEventTime(ctx);
          return;
        case "getRole100D":
          processGetRole100D(ctx);
          return;
        case "exchange":
          processMobiWebPayment(ctx);
          return;
        case "iapexchange":
          processIAPPayment(ctx);
          return;
        case "nc_exchange":
          processNCExchange(ctx);
          return;
        case "np_exchange":
          processNPExchange(ctx);
          return;
        case "updatePaymentPackage":
          processUpdatePaymentPackage(ctx);
          return;
        case "getConfig":
          processGetConfig(ctx);
          return;
        case "injectConstant":
          processInjectConstant(ctx);
          return;
        case "getLDB":
          processGetLDB(ctx);
          return;
        case "getSessionId":
          processGetSessionId(ctx);
          return;
        case "getPaymentInfo":
          getPaymentInfo(ctx);
          return;
        case "getShopInfo":
          getShopInfo(ctx);
          return;
        case "updateShopStatus":
          processUpdateShopStatus(ctx);
          return;
        case "getStats":
          processGetStats(ctx);
          return;
        case "planEvent":
          processPlanEvent(ctx);
          return;
        case "getNetaGroup":
          processGetNetaGroup(ctx);
          return;
        case "deleteNetaGroup":
          processDeleteNetaGroup(ctx);
          return;
        case "addNetaGroup":
          processAddNetaGroup(ctx);
          return;
        case "queryStats":
          processQueryStats(ctx);
          return;
        default:
          resp.put("msg", "unknown_cmd");
          ctx.reply(resp);
          break;
      }
    }
    catch (Exception e) {
      resp.put("msg", e.getMessage());

      ctx.reply(resp);
      LOG.globalException("node", String.format("InternalCall:%s", cmd), e);
    }
  }

  private void processDeleteNetaGroup(Message<JsonObject> ctx) {
    String groupId        = ctx.body().getString("groupId");
    NetaAPI.chatGroup.remove(groupId);
    JsonObject resp       = new JsonObject();
    JsonArray netaGroups  = new JsonArray();

    for (Map.Entry<String, NetaGroup> entry : NetaAPI.chatGroup.entrySet()) {
      JsonObject netaGroup = new JsonObject();
      netaGroup.put("groupId", entry.getKey());
      netaGroup.put("groupName", entry.getValue().groupName);
      netaGroup.put("member", entry.getValue().nMember);
      netaGroups.add(netaGroup);
    }

    resp.put("netaGroups", netaGroups);
    resp.put("msg", "ok");
    ctx.reply(resp);
  }

  private void processGetNetaGroup(Message<JsonObject> ctx) {
    JsonObject resp       = new JsonObject();
    JsonArray netaGroups  = new JsonArray();

    for (Map.Entry<String, NetaGroup> entry : NetaAPI.chatGroup.entrySet()) {
      JsonObject netaGroup = new JsonObject();
      netaGroup.put("groupId", entry.getKey());
      netaGroup.put("groupName", entry.getValue().groupName);
      netaGroup.put("member", entry.getValue().nMember);
      netaGroups.add(netaGroup);
    }

    resp.put("netaGroups", netaGroups);
    resp.put("msg", "ok");
    ctx.reply(resp);
  }

  private void processQueryStats(Message<JsonObject> ctx) {
    JsonObject resp       = new JsonObject();
    String date           = ctx.body().getString("date");
    String key = GlobalVariable.stringBuilder.get().append("daily_stats").append('_').append(date).toString();
    DailyStatsDAO dao = dailyStatsCB.load(key);
    if (dao == null)
      resp.put("msg", "not_found");
    else {
      JsonArray itemStats = new JsonArray();
      Map<Integer, Integer> dailyGain = dao.dailyGainItem;
      Map<Integer, Integer> dailyUse = dao.dailyUseItem;
      Map<Integer, Integer> dailyBacklog = dao.dailyBacklogItem;

      for (Map.Entry<Integer, Integer> entry : UserInventory.itemStats.entrySet()) {
        JsonObject itemStat = new JsonObject();
        int itemId = entry.getKey();
        itemStat.put("itemId", entry.getKey());
        itemStat.put("Tồn tổng", entry.getValue());
        itemStat.put("Tồn ngày", dailyBacklog.getOrDefault(itemId, 0));
        itemStat.put("Sử dụng trong ngày", dailyUse.getOrDefault(itemId, 0));
        itemStat.put("Phát sinh trong ngày", dailyGain.getOrDefault(itemId, 0));
        itemStats.add(itemStat);
      }

      resp.put("statItem", itemStats);
      resp.put("msg", "ok");
    }

    ctx.reply(resp);
  }

  private void processAddNetaGroup(Message<JsonObject> ctx) {
    JsonObject resp       = new JsonObject();
    String groupName = ctx.body().getString("groupName");
    NetaAPI.addGroup(groupName, ar -> {
      if (ar.result().equals("ok")) {
        JsonArray netaGroups  = new JsonArray();
        for (Map.Entry<String, NetaGroup> entry : NetaAPI.chatGroup.entrySet()) {
          JsonObject netaGroup = new JsonObject();
          netaGroup.put("groupId", entry.getKey());
          netaGroup.put("groupName", entry.getValue().groupName);
          netaGroup.put("member", entry.getValue().nMember);
          netaGroups.add(netaGroup);
        }

        resp.put("netaGroups", netaGroups);
      }
      resp.put("msg", ar.result());
      ctx.reply(resp);
    });
  }

  private void processPlanEvent(Message<JsonObject> ctx) {
    String eventPlan = ctx.body().getString("eventPlan");
    int eventType = Integer.parseInt(ctx.body().getString("eventType"));

    JsonObject resp = new JsonObject();

    Map<Integer, List<ExtendEventInfo>> curPlan;
    if (eventType == 0) {
      curPlan = TimingEvent.evtPlan;
    }
    else if (eventType == 1) {
      curPlan = IdolEvent.evtPlan;
    }
    else if (eventType == 2){
      curPlan = RankingEvent.evtPlan;
    }
    else {
      resp.put("msg", "invalid event type");
      ctx.reply(resp);
      return;
    }

    String res = Common.updatePlan(curPlan, eventPlan);
    resp.put("msg", res);

    if (res.equals("ok")) {
      JsonArray userEvent   = Transformer.transformEvents(TimingEvent.evtPlan, Transformer.userEvtId2Name);
      JsonArray idolEvent   = Transformer.transformEvents(IdolEvent.evtPlan, Transformer.idolEvtId2name);
      JsonArray rankEvent   = Transformer.transformEvents(RankingEvent.evtPlan, Transformer.rankEvtId2name);
      JsonArray groupEvent  = Transformer.transformEvent(GROUP_EVENT.evtMap, Transformer.groupEvtId2name);
      resp.put("userEvents", userEvent);
      resp.put("idolEvents", idolEvent);
      resp.put("rankEvents", rankEvent);
      resp.put("groupEvents", groupEvent);
    }

    ctx.reply(resp);
  }

  private void processGetStats(Message<JsonObject> ctx) {
    JsonObject resp = new JsonObject();
    JsonArray itemStats = new JsonArray();
    Map<Integer, Integer> dailyGain = DailyStats.inst().dailyGainItem;
    Map<Integer, Integer> dailyUse = DailyStats.inst().dailyUseItem;
    Map<Integer, Integer> dailyBacklog = DailyStats.inst().dailyBacklogItem;

    for (Map.Entry<Integer, Integer> entry : UserInventory.itemStats.entrySet()) {
      JsonObject itemStat = new JsonObject();
      int itemId = entry.getKey();
      itemStat.put("itemId", entry.getKey());
      itemStat.put("Tồn tổng", entry.getValue());
      itemStat.put("Tồn ngày", dailyBacklog.getOrDefault(itemId, 0));
      itemStat.put("Sử dụng trong ngày", dailyUse.getOrDefault(itemId, 0));
      itemStat.put("Phát sinh trong ngày", dailyGain.getOrDefault(itemId, 0));
      itemStats.add(itemStat);
    }

    resp.put("statItem", itemStats);
    resp.put("msg", "ok");
    ctx.reply(resp);
  }

  private void processUpdateShopStatus(Message<JsonObject> ctx) {
    JsonObject resp         = new JsonObject();
    int updatePID           = ctx.body().getInteger("updatePID");
    ShopData.ShopDto dto    = ShopData.shopDtoMap.get(updatePID);
    if (dto == null) {
      resp.put("msg", "invalid update package, wrong id");
      ctx.reply(resp);
      return;
    }

    if (dto.status == 1) {
      dto.status = 0;
    }
    else {
      dto.status = 1;
    }

    resp.put("shop", Transformer.transformShopData());
    resp.put("msg", "ok");
    ctx.reply(resp);
  }

  private void processUpdatePaymentPackage(Message<JsonObject> ctx) {
    JsonObject resp             = new JsonObject();
    String updatePID            = ctx.body().getString("updatePID");
    String updateTime           = ctx.body().getString("updateTime");
    String updateVIP            = ctx.body().getString("updateVIP");
    String updateItems          = ctx.body().getString("updateItems");

    PaymentData.PaymentDto dto = PaymentData.paymentDtoMap.get(updatePID);
    if (dto == null) {
      resp.put("msg", "invalid update package, wrong id");
      ctx.reply(resp);
      return;
    }

    int newTime, newVip;
    List<List<Integer>> newItems;
    try {
      newTime               = Integer.parseInt(updateTime);
      newVip                = Integer.parseInt(updateVIP);

      Type listOfListOfInt  = new TypeToken<List<List<Integer>>>() {}.getType();
      newItems              = Utilities.gson.fromJson(updateItems, listOfListOfInt);
    }
    catch (Exception e) {
      resp.put("msg", "invalid update value");
      ctx.reply(resp);
      return;
    }

    dto.reward  = newItems;
    dto.time    = newTime;
    dto.vip     = newVip;

    resp.put("msg", "ok");
    resp.put("payment", Transformer.transformPaymentData());
    ctx.reply(resp);
  }

  private void processGenNCExchangeLink(Message<JsonObject> ctx) throws Exception {
    String sessionId            = ctx.body().getString("sessionId");
    String amount               = ctx.body().getString("amount");
    JsonObject resp             = new JsonObject();
    resp.put("msg", "ok");
    resp.put("exchangeRequest", RequestGenerator.genNCExchangeRequest(sessionId, Integer.parseInt(amount)));
    ctx.reply(resp);
  }

  private void processGenNPExchangeLink(Message<JsonObject> ctx) throws Exception {
    String sessionId            = ctx.body().getString("sessionId");
    String amount               = ctx.body().getString("amount");
    JsonObject resp             = new JsonObject();
    resp.put("msg", "ok");
    resp.put("exchangeRequest", RequestGenerator.genNPExchangeRequest(sessionId, Integer.parseInt(amount)));
    ctx.reply(resp);
  }

  private void processGenWebPaymentLink(Message<JsonObject> ctx) throws Exception {
    String sessionId            = ctx.body().getString("sessionId");
    String packageId            = ctx.body().getString("packageId");
    PaymentData.PaymentDto dto  = PaymentData.paymentDtoMap.get(packageId);
    JsonObject resp             = new JsonObject();
    if (dto == null) {
      resp.put("msg", "payment package not found");
      ctx.reply(resp);
      return;
    }

    resp.put("msg", "ok");
    resp.put("paymentRequest", RequestGenerator.genPaymentRequest(sessionId, dto));
    ctx.reply(resp);
  }

  private void processGenIAPPaymentLink(Message<JsonObject> ctx) throws Exception {
    String sessionId            = ctx.body().getString("sessionId");
    String packageId            = ctx.body().getString("packageId");
    PaymentData.PaymentDto dto  = PaymentData.paymentDtoMap.get(packageId);
    JsonObject resp             = new JsonObject();
    if (dto == null) {
      resp.put("msg", "payment package not found");
      ctx.reply(resp);
      return;
    }

    resp.put("msg", "ok");
    resp.put("paymentRequest", RequestGenerator.genIAPPaymentRequest(sessionId, dto));
    ctx.reply(resp);
  }

  private void processGenGetRoleLink(Message<JsonObject> ctx) throws Exception {
    String sessionId            = ctx.body().getString("phoenixId");
    String serverId             = ctx.body().getString("serverId");
    JsonObject resp             = new JsonObject();

    resp.put("msg", "ok");
    resp.put("getRoleRequest", RequestGenerator.genGetRoleRequest(sessionId, serverId, (int)(System.currentTimeMillis()/1000)));
    ctx.reply(resp);
  }

  private void getShopInfo(Message<JsonObject> ctx) {
    JsonObject resp     = IntMessage.resp(ctx.body().getString("cmd"));
    resp.put("shop", Transformer.transformShopData());
    ctx.reply(resp);
  }

  private void getPaymentInfo(Message<JsonObject> ctx) {
    JsonObject resp     = IntMessage.resp(ctx.body().getString("cmd"));
    resp.put("payment", Transformer.transformPaymentData());
    ctx.reply(resp);
  }

  private void processSendPrivateMail(Message<JsonObject> ctx) {
    String title     = ctx.body().getString("mailTitle");
    String content   = ctx.body().getString("mailContent");
    String item      = ctx.body().getString("mailItems");
    String sessionId = ctx.body().getString("sessionId");

    JsonObject resp  = new JsonObject();

    loadSession(Integer.parseInt(sessionId), resp, sr -> {
      if (sr.succeeded()) {
        try {
          Session session = sr.result();

          Type listOfListOfInt = new TypeToken<List<List<Integer>>>() {}.getType();
          List<List<Integer>> r = Utilities.gson.fromJson(item, listOfListOfInt);
          if (r == null) {
            r = new ArrayList<>();
          }
          MailObj mailObj = MailObj.of(title, content, r, MailObj.MSG_TYPE_PRIVATE);
          session.userInbox.addPrivateMsg(mailObj);

          resp.put("msg", "ok");
          resp.put("session", Transformer.transformSession(session));
          if (resp.getString("state").equals("offline")) {
            CBSession.getInstance().sync(sessionId, session, ar -> {});
          }
          ctx.reply(resp);
        }
        catch (Exception e) {
          resp.put("msg", e.getMessage());
          ctx.reply(resp);
          LOG.globalException("node", "processSendPrivateMail",e);
        }
      }
      else {
        resp.put("msg", sr.cause().getMessage());
        ctx.reply(resp);
        LOG.globalException("node", "processSendPrivateMail",sr.cause());
      }
    });
  }

  private void processGetSessionId(Message<JsonObject> ctx) throws Exception {
    JsonObject resp       = IntMessage.resp(ctx.body().getString("cmd"));
    String userName       = ctx.body().getString("userName");
    String sid            = CBMapper.getInstance().getValue(Utilities.sha256Hash(userName.trim()));
    try {
      Integer.parseInt(sid);
      resp.put("sessionId", sid);
    }
    catch (Exception e) {
      resp.put("msg", "user not found");
    }
    ctx.reply(resp);
  }

  private void processGetLDB(Message<JsonObject> ctx) {
    JsonObject resp       = IntMessage.resp(ctx.body().getString("cmd"));
    int ldbId             = Integer.parseInt(ctx.body().getString("ldbId"));

    if (ldbId == LEADER_BOARD.TALENT_LDB_ID) {
      Transformer.transformLDB(LEADER_BOARD.TALENT_LDB_ID, ar -> {
        if (ar.succeeded()) {
          resp.put("ldb", ar.result());
          ctx.reply(resp);
        }
      });
    }
    else {
      Transformer.transformLDB(LEADER_BOARD.FIGHT_LDB_ID, ar -> {
        if (ar.succeeded()) {
          resp.put("ldb", ar.result());
          ctx.reply(resp);
        }
      });
    }
  }

  private void processInjectConstant(Message<JsonObject> ctx) {
    String path           = ctx.body().getString("path");
    String value          = ctx.body().getString("value");
    JsonObject resp       = IntMessage.resp(ctx.body().getString("cmd"));

    try {
      constantInjector.inject(path, value);

      resp.put("msg", "ok");
      resp.put("config", Transformer.transformConstant());
      ctx.reply(resp);
    }
    catch (Exception e) {
      resp.put("msg", e.getMessage());
      ctx.reply(resp);
      LOG.globalException("node", "injectConstant", e);
    }
  }

  private void processGetConfig(Message<JsonObject> ctx) {
    JsonObject resp     = IntMessage.resp(ctx.body().getString("cmd"));
    resp.put("config", Transformer.transformConstant());
    ctx.reply(resp);
  }

  private void processNCExchange(Message<JsonObject> ctx) {
    JsonObject resp   = new JsonObject();
    try {
      JsonObject req  = ctx.body();
      int sessionId   = req.getInteger("sessionId");
      int amount      = req.getInteger("amount");
      if (amount <= 0) {
        resp.put("status", EXCHANGE_FAIL_STATUS_CODE).put("msg", "Invalid Amount");
        ctx.reply(resp);
        LOG.paymentException("Node", "processNCExchange", String.format("Invalid amount sessionId:%d - amount:%d", sessionId, amount));
        return;
      }
      loadSession(sessionId, resp, sar -> {
        if (sar.succeeded()) {
          try {
            Session session = sar.result();
            if (session.userInventory.useItem(135, amount)) {
              boolean online  = resp.getString("state").equals("online");
              if (!online) {
                CBSession.getInstance().sync(Integer.toString(sessionId), session, ar -> {});
              }
              JsonObject data = new JsonObject();
              data.put("NetPoint",  session.userGameInfo.netPoint);
              data.put("NetCard",   session.userInventory.getItemCnt(135));

              resp.put("status", 1).put("msg", "Success");
              resp.put("data", data);
            }
            else {
              resp.put("status", INSUFFICIENT_AMOUNT).put("msg", "Insufficient Amount");
            }
          }
          catch (Exception e) {
            resp.put("status", EXCHANGE_FAIL_STATUS_CODE).put("msg", "Exchange fail");
            LOG.paymentException("Node", "processNCExchange", e);
          }
        }
        else {
          resp.put("status", ROLE_NAME_NOT_EXIST).put("msg", "Role name not exist");
          LOG.paymentException("Node", "processNCExchange", String.format("Role name not exist sessionId: %d", sessionId));
        }
        ctx.reply(resp);
      });

    }
    catch (Exception e) {
      resp.put("status", EXCHANGE_FAIL_STATUS_CODE).put("msg", "Exchange fail");
      ctx.reply(resp);
      LOG.paymentException("Node", "processNCExchange", e);
    }
  }

  private void processNPExchange(Message<JsonObject> ctx) {
    JsonObject resp   = new JsonObject();
    try {
      JsonObject req  = ctx.body();
      int sessionId   = req.getInteger("sessionId");
      int amount      = req.getInteger("amount");
      if (amount <= 0) {
        resp.put("status", EXCHANGE_FAIL_STATUS_CODE).put("msg", "Invalid Amount");
        ctx.reply(resp);
        LOG.paymentException("Node", "processNPExchange", String.format("Invalid amount sessionId:%d - amount:%d", sessionId, amount));
        return;
      }
      loadSession(sessionId, resp, sar -> {
        if (sar.succeeded()) {
          try {
            Session session = sar.result();

            if (session.userGameInfo.spendNetPoint(amount)) {
              boolean online  = resp.getString("state").equals("online");
              if (!online) {
                CBSession.getInstance().sync(Integer.toString(sessionId), session, ar -> {});
              }
              JsonObject data = new JsonObject();
              data.put("NetPoint",  session.userGameInfo.netPoint);
              data.put("NetCard",   session.userInventory.getItemCnt(135));

              resp.put("status", 1).put("msg", "Success");
              resp.put("data", data);
            }
            else {
              resp.put("status", INSUFFICIENT_AMOUNT).put("msg", "Insufficient Amount");
            }
          }
          catch (Exception e) {
            resp.put("status", EXCHANGE_FAIL_STATUS_CODE).put("msg", "Exchange fail");
            LOG.paymentException("Node", "processNPExchange", e);
          }
        }
        else {
          resp.put("status", ROLE_NAME_NOT_EXIST).put("msg", "Role name not exist");
          LOG.paymentException("Node", "processNPExchange", String.format("Role name not exist sessionId: %d", sessionId));
        }
        ctx.reply(resp);
      });
    }
    catch (Exception e) {
      resp.put("status", EXCHANGE_FAIL_STATUS_CODE).put("msg", "Exchange fail");
      ctx.reply(resp);
      LOG.paymentException("Node", "processNPExchange", e);
    }
  }

  private void processIAPPayment(Message<JsonObject> ctx) {
    JsonObject resp   = new JsonObject();

    try {
      JsonObject req  = ctx.body();
      Payload payload = Json.decodeValue(req.getString("payload"), Payload.class);
      PaymentData.PaymentDto dto = PaymentData.paymentDtoMap.get(payload.itemId);

      if (dto == null || dto.reward == null) {
        resp.put("status", -9).put("msg", "Exchange fail");
        ctx.reply(resp);
        LOG.paymentException("Node", "processIAPPayment", String.format("Payment item not found | invalid. sessionId:%d - itemId:%s", payload.sessionId, payload.itemId));
        return;
      }

      loadSession(payload.sessionId, resp, sar -> {
        if (sar.succeeded()) {
          try {
            Session session = sar.result();

            if (session.userPayment == null)
              session.userPayment = UserPayment.ofDefault();

            if (session.userPayment.isOrderLoop(payload.orderId) || session.userPayment.isIAPPayloadLoop(payload.iapTransId)) {
              resp.put("status", -4).put("msg", "Order Loop or payload loop");
              LOG.paymentException("Node", "processIAPPayment", String.format("Order Loop. sessionId:%d - itemId:%s - orderId:%s", payload.sessionId, payload.itemId, payload.orderId));
            }
            else {
              boolean online  = resp.getString("state").equals("online");

              PaymentHandler.IAPPaymentSuccess(session, payload, online, dto);
              resp.put("status", 1).put("msg", "Success");
            }
          }
          catch (Exception e) {
            resp.put("status", -9).put("msg", "Exchange fail");
            LOG.paymentException("Node", "processIAPPayment", e);
          }
        }
        else {
          resp.put("status", -8).put("msg", "Role name not exist");
          LOG.paymentException("Node", "processIAPPayment", String.format("Role name not exist sessionId: %d", payload.sessionId));
        }
        ctx.reply(resp);
      });
    }
    catch (Exception e) {
      resp.put("status", -9).put("msg", "Exchange fail");
      ctx.reply(resp);
      LOG.paymentException("Node", "processIAPPayment", e);
    }
  }

  /*100D Payment handle*/
  private void processMobiWebPayment(Message<JsonObject> ctx) {
    JsonObject resp   = new JsonObject();
    try {
      JsonObject req  = ctx.body();
      Payload payload = Json.decodeValue(req.getString("payload"), Payload.class);
      PaymentData.PaymentDto dto = PaymentData.paymentDtoMap.get(payload.itemId);

      if (dto == null || dto.reward == null) {
        resp.put("status", -9).put("msg", "Exchange fail");
        ctx.reply(resp);
        LOG.paymentException("Node", "processMobiWebPayment", String.format("Payment item not found | invalid. sessionId:%d - itemId:%s", payload.sessionId, payload.itemId));
        return;
      }

      loadSession(payload.sessionId, resp, sar -> {
        if (sar.succeeded()) {
          try {
            Session session = sar.result();

            if (session.userPayment == null)
              session.userPayment = UserPayment.ofDefault();

            if (session.userPayment.isOrderLoop(payload.orderId)) {
              resp.put("status", -4).put("msg", "Order Loop");
              LOG.paymentException("Node", "processMobiWebPayment", String.format("Order Loop. sessionId:%d - itemId:%s - orderId:%s", payload.sessionId, payload.itemId, payload.orderId));
            }
            else {
              boolean online  = resp.getString("state").equals("online");

              PaymentHandler._100DPaymentSuccess(session, payload, online, dto);
              resp.put("status", 1).put("msg", "Success");
            }
          }
          catch (Exception e) {
            resp.put("status", -9).put("msg", "Exchange fail");
            LOG.paymentException("Node", "processMobiWebPayment", e);
          }
        }
        else {
          resp.put("status", -8).put("msg", "Role name not exist");
          LOG.paymentException("Node", "processMobiWebPayment", String.format("Role name not exist sessionId: %d", payload.sessionId));
        }
        ctx.reply(resp);
      });
    }
    catch (Exception e) {
      resp.put("status", -9).put("msg", "Exchange fail");
      ctx.reply(resp);
      LOG.paymentException("Node", "processMobiWebPayment", e);
    }
  }

  /*GET ROLE 100D*/
  /*100DID -> sessionID*/
  private void processGetRole100D(Message<JsonObject> ctx) {
    String _100dId    = ctx.body().getString("100DID");

    String key        = String.format("100d_%s", _100dId);
    String sid        = CBMapper.getInstance().getValue(key);
    if (!Utilities.isValidString(sid))
      sid = "0";

    int sessionId     = Integer.parseInt(sid);

    JsonObject resp = new JsonObject();
    loadSession(sessionId, resp, sar -> {
      if (sar.succeeded()) {
        try {
          resp.put("msg", "ok");
          Session session = sar.result();
          JsonArray data = new JsonArray();
          data.add(new      JsonObject()
                  .put("ID",        sessionId)
                  .put("RoleName",  session.userGameInfo.displayName)
                  .put("Level",     session.userGameInfo.titleId)
                  .put("NetPoint",  session.userGameInfo.netPoint)
                  .put("NetCard",   session.userInventory.getItemCnt(135)));

          resp.put("getRoleData", data);
          ctx.reply(resp);
        }
        catch (Exception e) {
          resp.put("msg", e.getMessage());
          ctx.reply(resp);
        }
      }
      else {
        resp.put("msg", sar.cause().getMessage());
        ctx.reply(resp);
        String err = String.format("%s PHID: %s - SessionId: %d", sar.cause(), _100dId, sessionId);
        LOG.globalException("node", "getRole100D", err);
      }
    });
  }

  /*EVENTS*/
  private void processSetUserEventTime(Message<JsonObject> ctx) {
    String strEvt               = ctx.body().getString("eventList");
    String strStart             = ctx.body().getString("startDate");
    String strEnd               = ctx.body().getString("endDate");
    int flushDelay              = Integer.parseInt(ctx.body().getString("flushDelay"));
    int eventType               = Integer.parseInt(ctx.body().getString("eventType"));
    Type lsOfLsOfInt            = new TypeToken<List<List<Integer>>>() {}.getType();
    List<List<Integer>> uptInfo = Utilities.gson.fromJson(strEvt, lsOfLsOfInt);
    JsonObject resp             = IntMessage.resp(ctx.body().getString("cmd"));

    if (eventType < 0 || eventType > 3) {
      resp.put("msg", "invalid event type");
      ctx.reply(resp);
      return;
    }

    for (List<Integer> i : uptInfo)
      if (i.size() != 2) {
        resp.put("msg", "invalid event info format");
        ctx.reply(resp);
        return;
      }

    Map<Integer, ExtendEventInfo> evtMap;
    if (eventType == 0) {
      evtMap = TimingEvent.evtMap;
    }
    else if (eventType == 1) {
      evtMap = IdolEvent.evtMap;
    }
    else if (eventType == 2){
      evtMap = RankingEvent.evtMap;
    }
    else {
      evtMap  = GROUP_EVENT.evtMap;
      uptInfo.clear();
      uptInfo.addAll(Arrays.asList(
              Arrays.asList(GROUP_EVENT.GE_PROD_EVT_ID, 1),
              Arrays.asList(GROUP_EVENT.GE_GS_EVT_ID, 1),
              Arrays.asList(GROUP_EVENT.GE_CRZ_DEGREE_EVT_ID, 1),
              Arrays.asList(GROUP_EVENT.GE_MONTHLY_GC_EVT_ID,1)
      ));
    }

    for (List<Integer> evtInfo : uptInfo) {
      ExtendEventInfo ei = evtMap.get(evtInfo.get(0));
      if (ei != null) {
        int rewardPack = evtInfo.get(1);
        if (rewardPack < 1 || rewardPack > 4)
          rewardPack = 1;
        ei.updateTime(strStart, strEnd, flushDelay, rewardPack);
      }
    }

    JsonArray userEvent   = Transformer.transformEvents(TimingEvent.evtPlan, Transformer.userEvtId2Name);
    JsonArray idolEvent   = Transformer.transformEvents(IdolEvent.evtPlan, Transformer.idolEvtId2name);
    JsonArray rankEvent   = Transformer.transformEvents(RankingEvent.evtPlan, Transformer.rankEvtId2name);
    JsonArray groupEvent  = Transformer.transformEvent(GROUP_EVENT.evtMap, Transformer.groupEvtId2name);
    resp.put("userEvents", userEvent);
    resp.put("idolEvents", idolEvent);
    resp.put("rankEvents", rankEvent);
    resp.put("groupEvents", groupEvent);

    ctx.reply(resp);
  }

  private void processGetEvents(Message<JsonObject> ctx) {
    JsonObject resp     = IntMessage.resp(ctx.body().getString("cmd"));

    JsonArray userEvent   = Transformer.transformEvents(TimingEvent.evtPlan, Transformer.userEvtId2Name);
    JsonArray idolEvent   = Transformer.transformEvents(IdolEvent.evtPlan, Transformer.idolEvtId2name);
    JsonArray rankEvent   = Transformer.transformEvents(RankingEvent.evtPlan, Transformer.rankEvtId2name);
    JsonArray groupEvent  = Transformer.transformEvent(GROUP_EVENT.evtMap, Transformer.groupEvtId2name);
    resp.put("userEvents", userEvent);
    resp.put("idolEvents", idolEvent);
    resp.put("rankEvents", rankEvent);
    resp.put("groupEvents", groupEvent);
//
//    JsonArray userEvent = Transformer.transformEvent(TimingEvent.evtMap, Transformer.userEvtId2Name);
//    JsonArray rankEvent = Transformer.transformEvent(RankingEvent.evtMap, Transformer.rankEvtId2name);
//    JsonArray idolEvent = Transformer.transformEvent(IdolEvent.evtMap, Transformer.idolEvtId2name);
//    JsonArray groupEvent  = Transformer.transformEvent(GROUP_EVENT.evtMap, Transformer.groupEvtId2name);
//
//    resp.put("userEvents", userEvent);
//    resp.put("idolEvents", idolEvent);
//    resp.put("rankEvents", rankEvent);
//    resp.put("groupEvents", groupEvent);
    ctx.reply(resp);
  }
  /*EVENTS*/

  private void processSendMail(Message<JsonObject> ctx) {
    String title     = ctx.body().getString("mailTitle");
    String content   = ctx.body().getString("mailContent");
    String item      = ctx.body().getString("mailItems");
    JsonObject resp  = new JsonObject();

    try {
      addInbox(title, content, item);
      resp.put("msg", "ok");
      ctx.reply(resp);
    }
    catch (Exception e) {
      resp.put("msg", e.getMessage());
      ctx.reply(resp);
    }
  }

  private void addInbox(String title, String msg, String reward) {
    Type listOfListOfInt = new TypeToken<List<List<Integer>>>() {}.getType();
    List<List<Integer>> r = Utilities.gson.fromJson(reward, listOfListOfInt);
    if (r == null) {
      r = new ArrayList<>();
    }
    MailObj mailObj = MailObj.of(title, msg, r, MailObj.MSG_TYPE_PUBLIC);
    UserInbox.addPublicMessage(mailObj);
  }

  private void processInjectSession(Message<JsonObject> ctx) {
    String sessionId      = ctx.body().getString("sessionId");
    String path           = ctx.body().getString("path");
    String value          = ctx.body().getString("value");
    JsonObject resp       = IntMessage.resp(ctx.body().getString("cmd"));

    loadSession(Integer.parseInt(sessionId), resp, sr -> {
      if (sr.succeeded()) {
        try {
          Session session = sr.result();
          session.id      = Integer.parseInt(sessionId);
          session.userLDB = UserLDB.ofDefault();
          sessionInjector.inject(session, path, value);

          resp.put("msg", "ok");
          resp.put("session", Transformer.transformSession(session));
          if (resp.getString("state").equals("offline")) {
            CBSession.getInstance().sync(sessionId, session, ar -> {});
          }
          ctx.reply(resp);
        }
        catch (Exception e) {
          resp.put("msg", e.getMessage());
          ctx.reply(resp);
          LOG.globalException("node", "injectSession", e);
        }
      }
      else {
        resp.put("msg", sr.cause().getMessage());
        ctx.reply(resp);
        LOG.globalException("node", "injectSession", sr.cause());
      }
    });
  }

  private void processGetSession(Message<JsonObject> ctx) {
    String   strSessionId   = ctx.body().getString("sessionId");
    JsonObject  req         = ctx.body();
    JsonObject  resp        = IntMessage.resp(req.getString("cmd"));
    LOG.info("node", "InternalController:processGetSession", ctx.body());
    loadSession(Integer.parseInt(strSessionId), resp, sr -> {
      try {
        if (sr.succeeded()) {
          resp.put("msg", "ok");
          resp.put("session", Transformer.transformSession(sr.result()));
        }
        else {
          resp.put("msg", "session not found");
        }
      }
      catch (Exception e) {
        LOG.globalException("node", "InternalController:processGetSession", e);
        resp.put("msg", e.getMessage());
      }
      ctx.reply(resp);
    });
  }

  private void loadSession(int sessionId, JsonObject resp, Handler<AsyncResult<Session>> sr) {
    Session session   = SessionPool.getSessionFromPool(sessionId);
    if (session != null) {
      resp.put("state", "online");
      sr.handle(Future.succeededFuture(session));
    }
    else {
      CBSession.getInstance().load(Integer.toString(sessionId), ar -> {
        if (ar.succeeded()) {
          resp.put("state", "offline");
          sr.handle(Future.succeededFuture(ar.result()));
        }
        else {
          sr.handle(Future.failedFuture("session not found"));
        }
      });
    }
  }
}