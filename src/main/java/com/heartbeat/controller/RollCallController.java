package com.heartbeat.controller;

import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RollCallController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RollCallController.class);


  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));
      if (session != null) {
        ExtMessage resp;
        long curMs = System.currentTimeMillis();
        switch (cmd) {
          case "getRollCallInfo":
            resp = processGetRollCallInfo(session, curMs);
            break;
          case "claimDailyGift":
            resp = processClaimDailyGift(session, curMs);
            break;
          case "claimVipGift":
            resp = processClaimVipGift(session, curMs);
            break;
          default:
            resp = ExtMessage.daily_mission();
            resp.msg = "unknown_cmd";
            break;
        }

        resp.cmd = cmd;
        resp.timeChange = session.userGameInfo.timeChange;
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        session.effectResults.clear();
        session.userGameInfo.timeChange = false;
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

  private ExtMessage processClaimVipGift(Session session, long curMs) {
    ExtMessage resp     = ExtMessage.rollCall();
    resp.msg            = session.userRollCall.claimVipGift(session, curMs);
    resp.data.rollCall  = session.userRollCall;
    return resp;
  }

  private ExtMessage processClaimDailyGift(Session session, long curMs) {
    ExtMessage resp     = ExtMessage.rollCall();
    resp.msg            = session.userRollCall.claimDailyGift(session,curMs);
    resp.data.rollCall  = session.userRollCall;
    return resp;
  }

  private ExtMessage processGetRollCallInfo(Session session, long curMs) {
    ExtMessage resp     = ExtMessage.rollCall();
    resp.msg            = session.userRollCall.getRollCallInfo(session, curMs);
    resp.data.rollCall  = session.userRollCall;
    return resp;
  }
}
