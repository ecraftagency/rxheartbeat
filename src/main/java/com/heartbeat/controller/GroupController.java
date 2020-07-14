package com.heartbeat.controller;

import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));
      if (session != null) {
        ExtMessage resp = ExtMessage.group();
        switch (cmd) {
          case "idolInfo":
            break;
          case "idolLevelUp":
            break;
          case "addIdol":
            break;
          case "addAptByExp":
            break;
          case "addAptByItem":
            break;
          case "haloLevelUp":
            break;
          case "idolMaxLevelUnlock":
            break;
          default:
            resp = ExtMessage.group();
            resp.msg = "unknown_cmd";
            break;
        }
        resp.cmd = cmd;
        resp.data.idols = session.userIdol;
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        session.effectResults.clear();
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
}
