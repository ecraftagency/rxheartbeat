package com.heartbeat.controller;

import com.common.Constant;
import com.common.LOG;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class RankingController implements Handler<RoutingContext> {
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
          case "getAllRank":
            processGetAllRank(session, cmd, ctx);
            return;
          case "claimReward":
            processClaimReward(session, cmd, ctx);
            return;
          case "getRanking":
            processGetRanking(session, cmd, ctx);
            return;
          case "getRankingInfo":
            resp = processGetRankingInfo();
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
      LOG.globalException(e);
    }
  }

  private void processGetAllRank(Session session, String cmd, RoutingContext ctx) {
    session.userRanking.getAllRanking(session, ar -> {
      ExtMessage resp     = ExtMessage.ranking();
      resp.data.ranking   = session.userRanking;

      if (ar.succeeded()) {
        resp.data.extObj          = Json.encode(ar.result());
      }
      else {
        resp.msg = ar.cause().getMessage();
      }

      resp.cmd            = cmd;
      resp.timeChange     = session.userGameInfo.timeChange;
      resp.userRemainTime = session.userGameInfo.remainTime();
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      session.effectResults.clear();
      session.userGameInfo.timeChange = false;
    });
  }

  private void processClaimReward(Session session, String cmd, RoutingContext ctx) {
    int rankingType = ctx.getBodyAsJson().getInteger("rankingType");
    if (session.userGameInfo.isActiveTime()) {
      session.userRanking.claimReward(session, rankingType, ar -> {
        ExtMessage resp     = ExtMessage.ranking();
        resp.data.ranking   = session.userRanking;
        resp.effectResults  = session.effectResults;

        if (ar.succeeded()) {
          resp.msg          = ar.result();
        }
        else {
          resp.msg = ar.cause().getMessage();
        }

        resp.cmd            = cmd;
        resp.timeChange     = session.userGameInfo.timeChange;
        resp.userRemainTime = session.userGameInfo.remainTime();
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        session.effectResults.clear();
        session.userGameInfo.timeChange = false;
      });
    }
    else {
      ExtMessage resp     = ExtMessage.ranking();
      resp.data.ranking   = session.userRanking;
      resp.effectResults  = session.effectResults;

      resp.cmd            = cmd;
      resp.timeChange     = session.userGameInfo.timeChange;
      resp.userRemainTime = session.userGameInfo.remainTime();
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      session.effectResults.clear();
      session.userGameInfo.timeChange = false;
    }
  }

  private ExtMessage processGetRankingInfo() {
    ExtMessage resp         = ExtMessage.ranking();
    resp.data.extObj        = Json.encode(Constant.RANK_EVENT.evtMap);
    resp.serverTime         = (int)(System.currentTimeMillis()/1000);
    return resp;
  }

  private void processGetRanking(Session session, String cmd, RoutingContext ctx) {
    int rankingType = ctx.getBodyAsJson().getInteger("rankingType");
    session.userRanking.getRanking(rankingType, ar -> {
      ExtMessage resp   = ExtMessage.ranking();
      resp.cmd          = cmd;
      resp.data.ranking = session.userRanking;

      if (ar.succeeded()) {
        resp.data.extObj = Json.encode(ar.result());
      }
      else {
        resp.msg = ar.cause().getMessage();
      }

      resp.cmd        = cmd;
      resp.timeChange = session.userGameInfo.timeChange;
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      session.effectResults.clear();
      session.userGameInfo.timeChange = false;
    });
  }
}