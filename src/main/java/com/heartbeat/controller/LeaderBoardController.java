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

public class LeaderBoardController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(LeaderBoardController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null) {
        ExtMessage resp;
        switch (cmd) {
          case "submitScore":
            resp = processSubmitScore(session);
            break;
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
        resp.userRemainTime   = session.userGameInfo.time;

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

  private ExtMessage processSubmitScore(Session session) {
//    long totalCrt = session.userIdol.getTotalCreativity();
//    long totalPerf = session.userIdol.getTotalPerformance();
//    long totalAttr = session.userIdol.getTotalCreativity();
//    long totalTalent = totalCrt + totalPerf + totalAttr;
//
//    if (session.userLDB != null) {
//      session.userLDB.addLdbRecord(Constant.LEADER_BOARD.TALENT_LDB_ID, totalTalent);
//      session.userLDB.addLdbRecord(Constant.LEADER_BOARD.FIGHT_LDB_ID, session.userFight.currentFightLV.id);
//    }
    return ExtMessage.leaderBoard();
  }
}