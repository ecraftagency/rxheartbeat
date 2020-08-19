package com.heartbeat.controller;

import com.common.LOG;
import com.heartbeat.model.Session;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class InternalController implements Handler<Message<JsonObject>> {
  @Override
  public void handle(Message<JsonObject> ctx) {
    try {
      JsonObject json = ctx.body();
      String  cmd     = json.getString("cmd");

      switch (cmd) {
        case "ping":
          break;
        case "getSession":
          LOG.console("gmtool call");
          processGetSession(ctx);
          return;
        default:break;
      }
    }
    catch (Exception e) {
      LOG.globalException(e.getMessage());
    }

  }

  private void processGetSession(Message<JsonObject> ctx) {
    int sessionId                     = ctx.body().getInteger("sessionId");
    Session session                   = Session.of(sessionId);
    session.userGameInfo.displayName  = "blah blah";
    JsonObject resp = new JsonObject();
    resp.put("session", session);
    ctx.reply(resp);
  }
}
