package com.heartbeat.controller;

import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class SystemController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      LOGGER.error("test controller");
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));
      if (session != null) {
        long curMs = System.currentTimeMillis();
        ExtMessage resp;
        if (cmd.equals("heartbeat")) {
          resp = processHeartBeat(session, curMs);
        }
        else {
          resp = ExtMessage.system();
          resp.msg = "unknown_cmd";
        }
        resp.cmd = cmd;
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
      else {
        ctx.response().setStatusCode(401).end();
      }
    }
    catch (Exception e) {
      LOGGER.error(e.getMessage());
      ctx.response().setStatusCode(404).end();
    }
  }

  private ExtMessage processHeartBeat(Session session, long curMs) {
    session.updateOnline(System.currentTimeMillis());
    ExtMessage resp = ExtMessage.system();
    resp.serverTime = (int)(curMs/1000);
    return resp;
  }
}