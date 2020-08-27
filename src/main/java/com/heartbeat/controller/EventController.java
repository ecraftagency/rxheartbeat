package com.heartbeat.controller;

import com.common.Constant;
import com.common.LOG;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class EventController implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    String cmd          = "";
    try {
      cmd               = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      int lastIssued    = ctx.user().principal().getInteger("issueTime");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null && session.userProfile.lastLogin == lastIssued) {
        ExtMessage resp;
        long curMs = System.currentTimeMillis();
        switch (cmd) {
          case "claimUserEventReward":
            resp = processClaimUserEvtRwd(session, ctx, curMs);
            break;
          case "getUserEvents":
            resp = processGetUserEvt(session);
            break;
          case "getIdolEvents":
            resp = processGetIdolEvt();
            break;
          case "claimIdolEventReward":
            resp = processClaimIdolEvtRwd(session, ctx, curMs);
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
      ctx.response().setStatusCode(404).end();
      LOG.globalException("node", cmd, e);
    }
  }

  private ExtMessage processClaimIdolEvtRwd(Session session, RoutingContext ctx, long curMs) {
    ExtMessage resp     = ExtMessage.event();
    int idolId          = ctx.getBodyAsJson().getInteger("idolId");
    int evtId           = ctx.getBodyAsJson().getInteger("eventId");
    resp.msg            = session.userEvent.claimEventIdol(session, idolId, evtId, (int)(curMs/1000));
    resp.effectResults  = session.effectResults;
    resp.data.idols     = session.userIdol;
    return resp;
  }


  private ExtMessage processGetIdolEvt() {
    ExtMessage resp         = ExtMessage.event();
    resp.data.extObj        = Json.encode(Constant.IDOL_EVENT.evtMap);
    resp.serverTime         = (int)(System.currentTimeMillis()/1000);
    return resp;
  }

  private ExtMessage processClaimUserEvtRwd(Session session, RoutingContext ctx, long curMs) {
    int eventType         = ctx.getBodyAsJson().getInteger("eventType");
    int milestoneId       = ctx.getBodyAsJson().getInteger("milestoneId");
    ExtMessage resp       = ExtMessage.event();
    resp.data.event       = session.userEvent;

    if (session.userGameInfo.isActiveTime()) {
      resp.msg            = session.userEvent.claimEventReward(session, eventType, milestoneId, (int)(curMs/1000));
      resp.effectResults  = session.effectResults;
    }

    return resp;
  }

  private ExtMessage processGetUserEvt(Session session) {
    session.userEvent.reBalance();
    ExtMessage resp         = ExtMessage.event();
    resp.data.event         = session.userEvent;
    resp.data.extObj        = Json.encode(Constant.USER_EVENT.evtMap);
    resp.serverTime         = (int)(System.currentTimeMillis()/1000);
    return resp;
  }
}
