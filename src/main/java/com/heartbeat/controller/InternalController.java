package com.heartbeat.controller;

import com.common.LOG;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.service.SessionInjector;
import com.heartbeat.service.impl.EvilInjector;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

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
        case "inject":
          processInjectSession(ctx);
          break;
        case "getSession":
          processGetSession(ctx);
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

  private void processInjectSession(Message<JsonObject> ctx) {
    int sessionId     = ctx.body().getInteger("sessionId");
    String path       = ctx.body().getString("path");
    String value      = ctx.body().getString("value");
    JsonObject resp   = new JsonObject();

    loadSession(sessionId, resp, sr -> {
      if (sr.succeeded()) {
        try {
          Session session = sr.result();
          sessionInjector.inject(session, path, value);

          resp.put("msg", "ok");
          resp.put("session", Json.encode(sr.result()));
          if (resp.getString("ctx").equals("offline")) {
            CBSession.getInstance().sync(Integer.toString(sessionId), session, ar -> {});
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
    int         sessionId   = ctx.body().getInteger("sessionId");
    JsonObject  resp        = new JsonObject();
    loadSession(sessionId, resp, sr -> {
      if (sr.succeeded()) {
        resp.put("msg", "ok");
        resp.put("session", Json.encode(sr.result()));
      }
      else {
        resp.put("msg", "session not found");
      }
      ctx.reply(resp);
    });
  }

  private void loadSession(int sessionId, JsonObject ctx, Handler<AsyncResult<Session>> sr) {
    Session session   = SessionPool.getSessionFromPool(sessionId);
    if (session != null) {
      ctx.put("ctx", "online");
      sr.handle(Future.succeededFuture(session));
    }
    else {
      CBSession.getInstance().load(Integer.toString(sessionId), ar -> {
        if (ar.succeeded()) {
          ctx.put("ctx", "offline");
          sr.handle(Future.succeededFuture(ar.result()));
        }
        else {
          sr.handle(Future.failedFuture("session not found"));
        }
      });
    }
  }
}