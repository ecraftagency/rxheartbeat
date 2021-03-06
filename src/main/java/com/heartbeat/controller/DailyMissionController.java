package com.heartbeat.controller;

import com.common.LOG;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class DailyMissionController implements Handler<RoutingContext> {
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
        switch (cmd) {
          case "claimCrazyMilestone":
            resp = processClaimCrazyMilestone(session, ctx);
            break;
          case "getMissions":
            resp = processGetMissions(session);
            break;
          case "claimMissionReward":
            resp = processClaimMissionReward(session, ctx);
            break;
          default:
            resp = ExtMessage.daily_mission();
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

  private ExtMessage processClaimCrazyMilestone(Session session, RoutingContext ctx) {
    int milestone           = ctx.getBodyAsJson().getInteger("milestone");
    ExtMessage resp         = ExtMessage.daily_mission();
    resp.msg                = session.userGameInfo.claimCrazyReward(session, milestone);
    resp.effectResults      = session.effectResults;
    resp.data.gameInfo      = session.userGameInfo;
    resp.data.dailyMission  = session.userDailyMission;
    return resp;
  }

  private ExtMessage processClaimMissionReward(Session session, RoutingContext ctx) {
    ExtMessage resp         = ExtMessage.daily_mission();
    int missionId           = ctx.getBodyAsJson().getInteger("missionId");
    resp.msg                = session.userDailyMission.claimReward(session, missionId);
    resp.data.dailyMission  = session.userDailyMission;
    resp.data.gameInfo      = session.userGameInfo;
    resp.effectResults      = session.effectResults;
    return resp;
  }

  private ExtMessage processGetMissions(Session session) {
    ExtMessage resp         = ExtMessage.daily_mission();
    resp.msg                = "ok";
    resp.data.dailyMission  = session.userDailyMission;
    resp.data.gameInfo      = session.userGameInfo;
    return resp;
  }
}
