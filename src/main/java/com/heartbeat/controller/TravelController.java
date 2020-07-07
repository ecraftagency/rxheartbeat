package com.heartbeat.controller;

import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TravelController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TravelController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));
      if (session != null) {
        long curMs = System.currentTimeMillis();
        ExtMessage resp;
        switch (cmd) {
          case "travelInfo":
            resp = processTravelInfo(session, curMs);
            break;
          case "claimTravel":
            resp = processClaimTravel(session, curMs);
            break;
          default:
            resp = ExtMessage.travel();
            resp.msg = "unknown_cmd";
            break;
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

  private ExtMessage processTravelInfo(Session session, long curMs) {
    session.userTravel.updateTravel(session, curMs);
    ExtMessage resp = ExtMessage.travel();
    resp.data.travel = session.userTravel;
    resp.serverTime = (int)(curMs/1000);
    return resp;
  }

  private ExtMessage processClaimTravel(Session session, long curMs) {
    ExtMessage resp     = ExtMessage.travel();
    resp.msg            = session.userTravel.claimTravel(session, curMs);
    resp.data.travel    = session.userTravel;
    resp.effectResults  = session.effectResults;
    resp.serverTime     = (int)(curMs/1000);
    return resp;
  }
}