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

public class RankingController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RankingController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null) {
        ExtMessage resp;
        switch (cmd) {
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
      LOGGER.error(e.getCause().getMessage());
      ctx.response().setStatusCode(404).end();
    }
  }

  private ExtMessage processGetRankingInfo() {
    ExtMessage resp         = ExtMessage.ranking();
    resp.data.extObj        = Json.encode(Constant.RANKING.rankingInfo);
    resp.serverTime         = (int)(System.currentTimeMillis()/1000);
    return resp;
  }

  private void processGetRanking(Session session, String cmd, RoutingContext ctx) {
    int rankingType = ctx.getBodyAsJson().getInteger("rankingType");
    session.userRanking.getRanking(rankingType, ar -> {
      ExtMessage resp   = ExtMessage.ranking();
      resp.cmd = cmd;
      if (ar.succeeded()) {
        resp.data.extObj = Json.encode(ar.result());
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
      else {
        resp.msg = ar.cause().getMessage();
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
    });
  }
}