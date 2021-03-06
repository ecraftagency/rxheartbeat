package com.heartbeat.controller;

import com.common.Constant;
import com.common.LOG;
import com.common.Msg;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class TravelController implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    String cmd          = "";
    try {
      cmd               = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      int lastIssued    = ctx.user().principal().getInteger("issueTime");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null && session.userProfile.lastLogin == lastIssued) {
        long curMs = System.currentTimeMillis();
        ExtMessage resp;
        switch (cmd) {
          case "addMultiTravelClaim":
            resp = processAddMultiClaim(session, ctx);
            break;
          case "addTravelClaim":
            resp = processAddTravelClaim(session);
            break;
          case "travelInfo":
            resp = processTravelInfo(session, curMs);
            break;
          case "claimTravel":
            resp = processClaimTravel(session, curMs);
            break;
          case "claimMultiTravel":
            resp = processClaimMultiTravel(session, curMs);
            break;
          default:
            resp = ExtMessage.travel();
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

  private ExtMessage processAddMultiClaim(Session session, RoutingContext ctx) {
    ExtMessage resp     = ExtMessage.travel();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.TRAVEL_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    int amount          = ctx.getBodyAsJson().getInteger("amount");
    resp.msg            = session.userTravel.addMultiTravelClaim(session, amount);
    resp.data.travel    = session.userTravel;
    resp.data.gameInfo  = session.userGameInfo;
    return resp;
  }

  private ExtMessage processAddTravelClaim(Session session) {
    ExtMessage resp     = ExtMessage.travel();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.TRAVEL_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    resp.msg            = session.userTravel.addTravelClaim(session);
    resp.data.travel    = session.userTravel;
    resp.data.gameInfo  = session.userGameInfo;
    return resp;
  }

  private ExtMessage processTravelInfo(Session session, long curMs) {
    ExtMessage resp   = ExtMessage.travel();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.TRAVEL_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    session.userTravel.updateTravel(session, curMs);
    resp.data.travel  = session.userTravel;
    resp.serverTime   = (int)(curMs/1000);
    return resp;
  }

  private ExtMessage processClaimMultiTravel(Session session, long curMs) {
    ExtMessage resp         = ExtMessage.travel();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.TRAVEL_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    int currentTrvClaimCnt  = session.userTravel.currentTravelClaimCount;
    resp.msg                = session.userTravel.claimMultiTravel(session, curMs);
    resp.data.travel        = session.userTravel;
    resp.effectResults      = session.effectResults;
    resp.serverTime         = (int)(curMs/1000);
    resp.data.gameInfo      = session.userGameInfo;
    if (resp.msg.equals("ok") && currentTrvClaimCnt > 0) {
      session.userDailyMission.addRecord(Constant.DAILY_MISSION.TRAVEL_MISSION_TYPE, currentTrvClaimCnt);
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.TRAVEL_ACHIEVEMENT, currentTrvClaimCnt);
    }
    return resp;
  }


  private ExtMessage processClaimTravel(Session session, long curMs) {
    ExtMessage resp     = ExtMessage.travel();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.TRAVEL_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    resp.msg            = session.userTravel.claimTravel(session, curMs);
    resp.data.travel    = session.userTravel;
    resp.effectResults  = session.effectResults;
    resp.serverTime     = (int)(curMs/1000);
    resp.data.gameInfo  = session.userGameInfo;
    if (resp.msg.equals("ok")) {
      session.userDailyMission.addRecord(Constant.DAILY_MISSION.TRAVEL_MISSION_TYPE);
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.TRAVEL_ACHIEVEMENT, 1);
    }
    return resp;
  }
}
