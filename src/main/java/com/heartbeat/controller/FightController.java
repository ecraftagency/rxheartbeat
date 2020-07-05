package com.heartbeat.controller;

import com.heartbeat.common.Constant;
import com.heartbeat.common.Utilities;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class FightController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(FightController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));
      if (session != null) {
        ExtMessage resp;
        switch (cmd) {
          case "fightInfo":
            long curMs = System.currentTimeMillis();
            resp = processFightInfo(session, curMs);
            break;
          case "freeIdol":
            resp = processFreeIdol(session, ctx);
            break;
          case "fight":
            resp = processUserFight(session, ctx);
            break;
          case "gameShowFight":
            resp = processGameShowFight(session, ctx);
            break;
          case "freeGameShowIdol":
            resp = processFreeGameShowIdol(session, ctx);
            break;
          case "runShowFight":
            resp = processRunShowFight(session);
            break;
          case "shoppingFight":
            resp = processShoppingFight(session);
            break;
          default:
            resp = ExtMessage.fight();
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

  private ExtMessage processFightInfo(Session session, long curMs) {
    ExtMessage resp = ExtMessage.fight();
    resp.data.fight = session.userFight;
    resp.data.gameInfo  = session.userGameInfo;

    long firstOpenTime    = Utilities.certainSecond(Constant.SCHEDULE.gameShowOneOpenHour, 0, 0);
    long firstCloseTime   = Utilities.certainSecond(Constant.SCHEDULE.gameShowOneCloseHour, 0, 0);
    long secondOpenTime   = Utilities.certainSecond(Constant.SCHEDULE.gameShowTwoOpenHour, 0, 0);
    long secondCloseTime  = Utilities.certainSecond(Constant.SCHEDULE.gameShowTwoCloseHour, 0, 0);
    int deltaSec          = 0;

    if (curMs < firstOpenTime) {
      deltaSec = (int)(firstOpenTime - curMs)/1000;
    }
    else if (curMs > firstCloseTime && curMs < secondOpenTime) {
      deltaSec = (int)(secondOpenTime - curMs)/1000;
    }
    else if (curMs > secondCloseTime) { //qua ngày
      deltaSec = (int)(firstOpenTime - curMs + 86400000)/1000;
    }
    resp.data.fight.gameShowOpenCountDown = deltaSec;

    return resp;
  }

  private ExtMessage processFreeIdol(Session session, RoutingContext ctx) {
    ExtMessage resp     = ExtMessage.fight();

    int idolId          = ctx.getBodyAsJson().getInteger("idolId");
    resp.msg            = session.userFight.freeUsedIdol(session, idolId);
    resp.data.fight     = session.userFight;
    resp.data.inventory = session.userInventory;
    resp.data.gameInfo  = session.userGameInfo;

    return resp;
  }

  private ExtMessage processUserFight(Session session, RoutingContext ctx) {
    int idolId          = ctx.getBodyAsJson().getInteger("idolId");
    ExtMessage resp     =  ExtMessage.fight();
    resp.msg = session.userFight.handleFight(session, idolId);

    resp.effectResults  = session.effectResults;
    resp.data.fight     = session.userFight;
    resp.data.gameInfo  = session.userGameInfo;
    return resp;
  }

  private ExtMessage processGameShowFight(Session session, RoutingContext ctx) {
    ExtMessage resp       =  ExtMessage.fight();

    if (Constant.SCHEDULE.gameShowOpen) {
      int idolId          = ctx.getBodyAsJson().getInteger("idolId");
      resp.msg            = session.userFight.handleGameShowFight(session, idolId);
      resp.effectResults  = session.effectResults;
      resp.data.fight     = session.userFight;
      resp.data.gameInfo  = session.userGameInfo;
    }

    return resp;
  }

  private ExtMessage processFreeGameShowIdol(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.fight();

    if (Constant.SCHEDULE.gameShowOpen) {
      int idolId          = ctx.getBodyAsJson().getInteger("idolId");
      resp.msg            = session.userFight.freeUsedGameShowIdol(session, idolId);
      resp.data.fight     = session.userFight;
      resp.data.inventory = session.userInventory;
      resp.data.gameInfo  = session.userGameInfo;
    }
    else {
      resp.msg = "game_show_time_out";
    }

    return resp;
  }

  private ExtMessage processRunShowFight(Session session) {
    ExtMessage resp     = ExtMessage.fight();
    resp.msg            = session.userFight.handleRunShowFight(session);
    resp.data.fight     = session.userFight;
    resp.data.gameInfo  = session.userGameInfo;
    resp.effectResults  = session.effectResults;
    return resp;
  }

  private ExtMessage processShoppingFight(Session session) {
    ExtMessage resp     = ExtMessage.fight();
    resp.msg            = session.userFight.handleShoppingFight(session);
    resp.data.fight     = session.userFight;
    resp.data.gameInfo  = session.userGameInfo;
    resp.effectResults  = session.effectResults;
    return resp;
  }
}
