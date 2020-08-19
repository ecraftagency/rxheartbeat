package com.heartbeat.controller;

import com.common.LOG;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.service.SessionInjector;
import com.heartbeat.service.impl.EvilInjector;
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
        case "ping":
          break;
        case "inject":
          processInjectSession(ctx);
          break;
        case "getSession":
          processGetSession(ctx);
          return;
        default:break;
      }
    }
    catch (Exception e) {
      resp.put("msg", e.getMessage());
      ctx.reply(resp);
      LOG.globalException(e);
    }

  }

  private void processInjectSession(Message<JsonObject> ctx) {
    int id          = ctx.body().getInteger("id");
    String path     = ctx.body().getString("path");
    String value    = ctx.body().getString("value");
    Session session = SessionPool.getSessionFromPool(id);
    JsonObject resp = new JsonObject();

    if (session != null) {
      try {
        sessionInjector.inject(session, path, value);
        resp.put("msg", "ok");
        ctx.reply(resp);
      }
      catch (Exception e) {
        resp.put("msg", e.getMessage());
        ctx.reply(resp);
      }
    }
  }

  private void processGetSession(Message<JsonObject> ctx) {
    int sessionId                     = ctx.body().getInteger("sessionId");
    Session session                   = SessionPool.getSessionFromPool(sessionId);
    JsonObject resp = new JsonObject();
    if (session != null) {
      resp.put("msg", "ok");
      resp.put("session", Json.encode(session));
    }
    else {
      resp.put("msg", "session not found");
    }
    ctx.reply(resp);
  }
}
