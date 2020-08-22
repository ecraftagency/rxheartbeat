package com.heartbeat.internal;

import com.common.LOG;
import com.common.Utilities;
import com.google.gson.reflect.TypeToken;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserInbox;
import com.heartbeat.service.SessionInjector;
import com.heartbeat.service.impl.EvilInjector;
import com.transport.IntMessage;
import com.transport.model.MailObj;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InternalController implements Handler<Message<JsonObject>> {
  SessionInjector sessionInjector;

  public InternalController() {
    sessionInjector = new EvilInjector();
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
        default:
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