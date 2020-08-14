package com.heartbeat.controller;

import com.heartbeat.common.Constant;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      int lastIssued    = ctx.user().principal().getInteger("issueTime");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null && session.userProfile.lastLogin == lastIssued) {
        ExtMessage resp;
        switch (cmd) {
          case "claimEventReward":
            resp = processClaimAchievement(session, ctx);
            break;
          case "getEvents":
            resp = processGetEvents(session);
            break;
          default:
            resp = ExtMessage.event();
            resp.msg = "unknown_cmd";
            break;
        }

        resp.cmd            = cmd;
        resp.timeChange     = session.userGameInfo.timeChange;
        resp.userRemainTime = session.userGameInfo.remainTime();

        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        session.effectResults.clear();
        session.userGameInfo.timeChange = false;
      }
      else {
        ctx.response().setStatusCode(401).end();
      }
    }
    catch (Exception e) {
      LOGGER.error(e.getCause().getMessage());
      ctx.response().setStatusCode(404).end();
    }
  }

  private ExtMessage processClaimAchievement(Session session, RoutingContext ctx) {
    int eventType           = ctx.getBodyAsJson().getInteger("eventType");
    int milestoneId         = ctx.getBodyAsJson().getInteger("milestoneId");
    ExtMessage resp         = ExtMessage.event();
    resp.data.event         = session.userEvent;

    if (session.userGameInfo.isActiveTime()) {
      int second              = (int)(System.currentTimeMillis()/1000);
      resp.msg                = session.userEvent.claimEventReward(session, eventType, milestoneId, second);
      resp.effectResults      = session.effectResults;
    }

    return resp;
  }

  private ExtMessage processGetEvents(Session session) {
    session.userEvent.reBalance();
    ExtMessage resp         = ExtMessage.event();
    resp.data.event         = session.userEvent;
    resp.data.extObj        = Json.encode(Constant.EVENT.eventInfoMap);
    resp.serverTime         = (int)(System.currentTimeMillis()/1000);
    return resp;
  }
}
