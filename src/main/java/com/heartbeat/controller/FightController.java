package com.heartbeat.controller;

import com.common.Constant;
import com.common.LOG;
import com.common.Msg;
import com.common.Utilities;
import com.heartbeat.event.RankingEvent;
import com.heartbeat.event.TimingEvent;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGroup;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class FightController implements Handler<RoutingContext> {
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
          case "multiRunShowFight":
            resp = processMultiRunShowFight(session, ctx);
            break;
          case "shoppingFight":
            resp = processShoppingFight(session);
            break;
          case "multiShoppingFight":
            resp = processMultiShoppingFight(session, ctx);
            break;
          default:
            resp = ExtMessage.fight();
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

  private ExtMessage processMultiShoppingFight(Session session, RoutingContext ctx) {
    int time            = ctx.getBodyAsJson().getInteger("time");
    ExtMessage resp     = ExtMessage.fight();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.FAST_SHOPPING_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    resp.msg            = session.userFight.handleMultiShoppingFight(session, time);
    resp.data.fight     = session.userFight;
    resp.data.gameInfo  = session.userGameInfo;
    resp.effectResults  = session.effectResults;

    if (resp.msg.equals("ok")) {
      session.userDailyMission.addRecord(Constant.DAILY_MISSION.SHOPPING_MISSION_TYPE, time);
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.SHOPPING_ACHIEVEMENT, time);
    }
    return resp;
  }

  private ExtMessage processMultiRunShowFight(Session session, RoutingContext ctx) {
    int time            = ctx.getBodyAsJson().getInteger("time");
    ExtMessage resp     = ExtMessage.fight();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.FAST_RUN_SHOW_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    resp.msg            = session.userFight.handleMultiRunShowFight(session, time);
    resp.data.fight     = session.userFight;
    resp.data.gameInfo  = session.userGameInfo;
    resp.effectResults  = session.effectResults;
    if (resp.msg.equals("ok")) {
      session.userDailyMission.addRecord(Constant.DAILY_MISSION.RUN_SHOW_MISSION_TYPE, time);
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.RUN_SHOW_ACHIEVEMENT, time);
    }
    return resp;
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
    resp.data.inventory = session.userInventory.updateAndGet();
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
    if (resp.msg.equals("ok")) {
      session.userDailyMission.addRecord(Constant.DAILY_MISSION.FIGHT_MISSION_TYPE);
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.FIGHT_ACHIEVEMENT, 1);
      session.userRanking.addEventRecord(RankingEvent.FIGHT_RANK_ID, 1);
    }
    return resp;
  }

  private ExtMessage processGameShowFight(Session session, RoutingContext ctx) {
    ExtMessage resp       =  ExtMessage.fight();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.GAME_SHOW_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    if (Constant.SCHEDULE.gameShowOpen) {
      int idolId          = ctx.getBodyAsJson().getInteger("idolId");
      resp.msg            = session.userFight.handleGameShowFight(session, idolId);
      resp.effectResults  = session.effectResults;
      resp.data.fight     = session.userFight;
      resp.data.gameInfo  = session.userGameInfo;
      if (resp.msg.equals("ok")) {
        UserGroup group = GroupPool.getGroupFromPool(session.groupID);
        if (group != null)
          group.addRecord(session, Constant.GROUP_EVENT.GE_GS_EVT_ID, 1, true);

        session.userDailyMission.addRecord(Constant.DAILY_MISSION.GAME_SHOW_MISSION_TYPE);
        session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.GAME_SHOW_ACHIEVEMENT, 1);
        session.userEvent.addEventRecord(TimingEvent.GAME_SHOW_EVT_ID, 1);
      }
    }

    return resp;
  }

  private ExtMessage processFreeGameShowIdol(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.fight();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.GAME_SHOW_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    if (Constant.SCHEDULE.gameShowOpen) {
      int idolId          = ctx.getBodyAsJson().getInteger("idolId");
      resp.msg            = session.userFight.freeUsedGameShowIdol(session, idolId);
      resp.data.fight     = session.userFight;
      resp.data.inventory = session.userInventory.updateAndGet();
      resp.data.gameInfo  = session.userGameInfo;
    }
    else {
      resp.msg = Msg.map.getOrDefault(Msg.GAME_SHOW_TIMEOUT, "game_show_time_out");
    }

    return resp;
  }

  private ExtMessage processRunShowFight(Session session) {
    ExtMessage resp     = ExtMessage.fight();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.RUN_SHOW_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    resp.msg            = session.userFight.handleRunShowFight(session);
    resp.data.fight     = session.userFight;
    resp.data.gameInfo  = session.userGameInfo;
    resp.effectResults  = session.effectResults;
    if (resp.msg.equals("ok")) {
      session.userDailyMission.addRecord(Constant.DAILY_MISSION.RUN_SHOW_MISSION_TYPE);
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.RUN_SHOW_ACHIEVEMENT, 1);
    }
    return resp;
  }

  private ExtMessage processShoppingFight(Session session) {
    ExtMessage resp     = ExtMessage.fight();

    if (session.userGameInfo.titleId < Constant.UNLOCK_FUNCTION.SHOPPING_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    resp.msg            = session.userFight.handleShoppingFight(session);
    resp.data.fight     = session.userFight;
    resp.data.gameInfo  = session.userGameInfo;
    resp.effectResults  = session.effectResults;
    if (resp.msg.equals("ok")) {
      session.userDailyMission.addRecord(Constant.DAILY_MISSION.SHOPPING_MISSION_TYPE);
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.SHOPPING_ACHIEVEMENT, 1);
    }
    return resp;
  }
}
