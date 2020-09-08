package com.heartbeat.controller;

import com.common.LOG;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class LeaderBoardController implements Handler<RoutingContext> {
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
          case "getLeaderBoard":
            processGetLeaderBoard(session, cmd, ctx);
            return;
          default:
            resp = ExtMessage.event();
            resp.msg = "unknown_cmd";
            break;
        }

        resp.cmd              = cmd;
        resp.timeChange       = session.userGameInfo.timeChange;
        resp.userRemainTime   = session.userGameInfo.remainTime();

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

  private void processGetLeaderBoard(Session session, String cmd, RoutingContext ctx) {
    int ldbType         = ctx.getBodyAsJson().getInteger("ldbType"); //0 bhx tài năng, 1 bxh đi ải
    session.userLDB.getLeaderBoard(ldbType, ar -> {
      ExtMessage resp   = ExtMessage.leaderBoard();
      resp.cmd          = cmd;

      if (ar.succeeded()) {
        resp.data.extObj = Json.encode(ar.result());
      }
      else {
        resp.msg = ar.cause().getMessage();
      }

      resp.cmd = cmd;
      resp.timeChange = session.userGameInfo.timeChange;
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      session.effectResults.clear();
      session.userGameInfo.timeChange = false;
    });
  }
}