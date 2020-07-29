package com.heartbeat.controller;

import com.heartbeat.common.Constant;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserIdol;
import com.transport.ExtMessage;
import com.transport.model.Idols;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class IdolController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(IdolController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));
      if (session != null) {
        ExtMessage resp;
        switch (cmd) {
          case "idolInfo":
            resp = processGetIdolInfo(session);
            break;
          case "idolLevelUp": //async
            resp = processIdolLevelUp(session, ctx);
            break;
          case "addIdol":
            resp = processAddIdol(session, ctx);
            break;
          case "addAptByExp": //up tư chất = exp tư chất
            resp = processIdolAptAddByExp(session, ctx);
            break;
          case "addAptByItem":
            resp = processAptUpByItem(session, ctx);
            break;
          case "haloLevelUp":
            resp = processHaloLevelUp(session, ctx);
            break;
          case "idolMaxLevelUnlock":
            resp = processUnlockIdolMaxLevel(session, ctx);
            break;
          default:
            resp = ExtMessage.idol();
            resp.msg = "unknown_cmd";
            break;
        }
        resp.data.idols = session.userIdol;
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

  private ExtMessage processGetIdolInfo(Session session) {
    ExtMessage resp = ExtMessage.idol();
    resp.msg = "ok";
    resp.data.gameInfo = session.userGameInfo;
    resp.data.production = session.userProduction;
    return resp;
  }

  private ExtMessage processIdolLevelUp(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.idol();
    int idolID = ctx.getBodyAsJson().getInteger("idolID");
    resp.msg = session.userIdol.levelUp(session, idolID);
    resp.data.gameInfo = session.userGameInfo;
    resp.data.production = session.userProduction;
    if (resp.msg.equals("ok")) {
      session.userDailyMission.addRecord(Constant.DAILY_MISSION.IDOL_LV_MISSION_TYPE);
    }
    return resp;
  }

  private ExtMessage processAddIdol(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.idol();
    int newIdolId = ctx.getBodyAsJson().getInteger("idolId");
    Idols.Idol idol = UserIdol.buildIdol(newIdolId);
    if (session.userIdol.addIdol(idol)) {
      resp.msg = "ok";
    }
    else {
      resp.msg = "add_idol_fail";
    }

    if (resp.msg.equals("ok")) {
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.IDOL_ACHIEVEMENT, 1);
    }
    return resp;
  }

  private ExtMessage processIdolAptAddByExp(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.idol();
    int idolId      = ctx.getBodyAsJson().getInteger("idolId");
    int speciality  = ctx.getBodyAsJson().getInteger("speciality"); //[2 - sang tao, 3 - bieu dien, 4 - cuon hut]
    resp.msg        = session.userIdol.addAptByExp(idolId, speciality);
    if (resp.msg.equals("ok")) {
      session.userDailyMission.addRecord(Constant.DAILY_MISSION.IDOL_APT_MISSION_TYPE);
    }
    return resp;
  }

  private ExtMessage processAptUpByItem(Session session, RoutingContext ctx) {
    ExtMessage resp     = ExtMessage.idol();
    int idolId          = ctx.getBodyAsJson().getInteger("idolId");
    int speciality      = ctx.getBodyAsJson().getInteger("speciality");
    int step            = ctx.getBodyAsJson().getInteger("step");
    resp.msg            = session.userIdol.addAptByItem(session, idolId, speciality, step);
    resp.data.inventory = session.userInventory;
    if (resp.msg.equals("ok")) {
      session.userAchievement.addAchieveRecord(67*100, 1);
    }
    return resp;
  }

  private ExtMessage processHaloLevelUp(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.idol();
    int idolId = ctx.getBodyAsJson().getInteger("idolId");
    int haloId = ctx.getBodyAsJson().getInteger("haloId");

    resp.msg = session.userIdol.idolPersonalHaloLevelUp(session, idolId, haloId);
    if (resp.msg.equals("ok")) {
      resp.data.gameInfo = session.userGameInfo;
      resp.data.idols = session.userIdol;
      resp.data.inventory = session.userInventory;
    }
    return resp;
  }

  private ExtMessage processUnlockIdolMaxLevel(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.idol();
    int idolId = ctx.getBodyAsJson().getInteger("idolId");
    resp.msg = session.userIdol.idolMaxLevelUnlock(session, idolId);
    return resp;
  }
}
