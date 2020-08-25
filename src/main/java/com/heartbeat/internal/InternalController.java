package com.heartbeat.internal;

import com.common.LOG;
import com.common.Utilities;
import com.gateway.model.Payload;
import com.google.gson.reflect.TypeToken;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserInbox;
import com.heartbeat.service.ConstantInjector;
import com.heartbeat.service.SessionInjector;
import com.heartbeat.service.impl.EvilInjector;
import com.heartbeat.service.impl.MaydayInjector;
import com.transport.IntMessage;
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

public class InternalController implements Handler<Message<JsonObject>> {
  SessionInjector sessionInjector;
  ConstantInjector constantInjector;
  public InternalController() {
    sessionInjector = new EvilInjector();
    constantInjector = new MaydayInjector();
  }

  @Override
  public void handle(Message<JsonObject> ctx) {
    JsonObject resp = new JsonObject();
    try {
      JsonObject json = ctx.body();
      String  cmd     = json.getString("cmd");
      switch (cmd) {
        case "injectSession":
          processInjectSession(ctx);
          break;
        case "getSession":
          processGetSession(ctx);
          return;
        case "sendMail":
          processSendMail(ctx);
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
        case "getConfig":
          processGetConfig(ctx);
          return;
        case "injectConstant":
          processInjectConstant(ctx);
          return;
        case "getLDB":
          processGetLDB(ctx);
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
      LOG.globalException(e);
    }
  }

  private void processGetLDB(Message<JsonObject> ctx) {
    JsonObject resp       = IntMessage.resp(ctx.body().getString("cmd"));
    Transformer.transformLDB(LEADER_BOARD.TALENT_LDB_ID, ar -> {
      if (ar.succeeded()) {
        resp.put("ldb", ar.result());
        ctx.reply(resp);
      }
    });
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
      LOG.globalException(e);
    }
  }

  private void processGetConfig(Message<JsonObject> ctx) {
    JsonObject resp     = IntMessage.resp(ctx.body().getString("cmd"));
    resp.put("config", Transformer.transformConstant());
    ctx.reply(resp);
  }

  /*100D Payment handle*/
  private void processMobiWebPayment(Message<JsonObject> ctx) {
    JsonObject resp   = new JsonObject();
    try {
      JsonObject req  = ctx.body();
      Payload payload = Json.decodeValue(req.getString("payload"), Payload.class);
      loadSession(payload.sessionId, resp, sar -> {
        if (sar.succeeded()) {
          try {
            Session session = sar.result();
            boolean online  = resp.getString("state").equals("online");
            PaymentHandler.mobiPaymentSuccess(session, payload, online);
            resp.put("code", 0).put("msg", "Success");
          }
          catch (Exception e) {
            resp.put("code", -9).put("msg", "Exchange fail");
            LOG.paymentException(e);
          }
        }
        else {
          resp.put("code", -8).put("msg", "Role name not exist");
          LOG.paymentException(String.format("Role name not exist sessionId: %d", payload.sessionId));
        }
        ctx.reply(resp);
      });
    }
    catch (Exception e) {
      resp.put("code", -9).put("msg", "Exchange fail");
      ctx.reply(resp);
      LOG.paymentException(e);
    }
  }

  /*GET ROLE 100D*/
  /*100DID -> sessionID*/
  private void processGetRole100D(Message<JsonObject> ctx) {
    int sessionId   = 2000000; //todo hardcode [sessionId = 1002SessionId(ctx.getString("100DID"))]
    JsonObject resp = new JsonObject();
    loadSession(sessionId, resp, sar -> {
      if (sar.succeeded()) {
        try {
          resp.put("msg", "ok");
          Session session = sar.result();
          resp.put("getRoleData", new      JsonObject()
                  .put("ID",        Integer.toString(sessionId))
                  .put("RoleName",  session.userGameInfo.displayName)
                  .put("Level",     Integer.toString(session.userGameInfo.titleId)));
          ctx.reply(resp);
        }
        catch (Exception e) {
          resp.put("msg", e.getMessage());
          ctx.reply(resp);
        }
      }
      else {
        resp.put("msg", sar.cause().getMessage());
      }
    });
  }

  /*EVENTS*/
  private void processSetUserEventTime(Message<JsonObject> ctx) {
    String strEvt         = ctx.body().getString("eventList");
    String strStart       = ctx.body().getString("startDate");
    String strEnd         = ctx.body().getString("endDate");
    int eventType         = Integer.parseInt(ctx.body().getString("eventType"));
    Type listOfInt        = new TypeToken<List<Integer>>() {}.getType();
    List<Integer> events  = Utilities.gson.fromJson(strEvt, listOfInt);
    JsonObject resp       = IntMessage.resp(ctx.body().getString("cmd"));

    if (eventType < 0 || eventType > 2) {
      resp.put("msg", "invalid event type");
      ctx.reply(resp);
      return;
    }

    if (eventType == 0) {
      for (Integer eventId : events)
        USER_EVENT.evtMap.computeIfPresent(eventId, (k, v) -> v.updateEventTime(strStart, strEnd));
    }
    else if (eventType == 1) {
      for (Integer eventId : events)
        IDOL_EVENT.evtMap.computeIfPresent(eventId, (k, v) -> v.updateEventTime(strStart, strEnd));
    }
    else {
      RANK_EVENT.rankingInfo.setRankingTime(strStart, strEnd);
    }

    JsonArray userEvent = Transformer.transformUserEvent();
    JsonArray idolEvent = Transformer.transformIdolEvent();
    JsonArray rankEvent = Transformer.transformRankingEvent();
    resp.put("userEvents", userEvent);
    resp.put("idolEvents", idolEvent);
    resp.put("rankEvents", rankEvent);
    ctx.reply(resp);
  }

  private void processGetEvents(Message<JsonObject> ctx) {
    JsonObject resp     = IntMessage.resp(ctx.body().getString("cmd"));
    JsonArray userEvent = Transformer.transformUserEvent();
    JsonArray idolEvent = Transformer.transformIdolEvent();
    JsonArray rankEvent = Transformer.transformRankingEvent();

    resp.put("userEvents", userEvent);
    resp.put("idolEvents", idolEvent);
    resp.put("rankEvents", rankEvent);

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
          LOG.globalException(e);
        }
      }
      else {
        resp.put("msg", sr.cause().getMessage());
        ctx.reply(resp);
      }
    });
  }

  private void processGetSession(Message<JsonObject> ctx) {
    String   strSessionId   = ctx.body().getString("sessionId");
    JsonObject  req         = ctx.body();
    JsonObject  resp        = IntMessage.resp(req.getString("cmd"));
    loadSession(Integer.parseInt(strSessionId), resp, sr -> {
      if (sr.succeeded()) {
        resp.put("msg", "ok");
        resp.put("session", Transformer.transformSession(sr.result()));
      }
      else {
        resp.put("msg", "session not found");
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