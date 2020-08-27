package com.heartbeat.controller;

import com.common.Constant;
import com.common.LOG;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import static com.common.Constant.*;

public class MediaController implements Handler<RoutingContext> {
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
          case "mediaInfo":
            resp = processGetMediaInfo(session, curMs);
            break;
          case "claimMedia": //async
            resp = processClaimMedia(session, ctx);
            break;
          case "addMediaCount":
            resp = processAddMediaCount(session, curMs, ctx);
            break;
          default:
            resp = ExtMessage.media();
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

  private ExtMessage processAddMediaCount(Session session, long curMs, RoutingContext ctx) {
    int amount = ctx.getBodyAsJson().getInteger("amount");
    ExtMessage resp = ExtMessage.media();
    if (amount <= 0) {
      resp.msg = "wrong_amount";
      return resp;
    }

    if (!session.userInventory.haveItem(USER_GAME_INFO.MEDIA_CONTRACT_ITEM, amount)) {
      resp.msg = "insufficient_item";
      return resp;
    }

    session.userGameInfo.updateUserMedia(curMs);
    session.userInventory.useItem(USER_GAME_INFO.MEDIA_CONTRACT_ITEM, amount);
    session.userGameInfo.addMediaClaim(amount);
    resp.data.gameInfo = session.userGameInfo;
    resp.msg = "ok";
    //record
    session.userAchievement.addAchieveRecord(ACHIEVEMENT.MEDIA_CONTRACT_USE, amount);
    return resp;
  }

  private ExtMessage processClaimMedia(Session session, RoutingContext ctx) {
    int answer          = ctx.getBodyAsJson().getInteger("answer");
    ExtMessage resp     = ExtMessage.media();
    resp.msg            = session.userGameInfo.claimMedia(session, answer);
    resp.data.gameInfo  = session.userGameInfo;
    resp.effectResults  = session.effectResults;
    if (resp.msg.equals("ok")) {
      session.userDailyMission.addRecord(Constant.DAILY_MISSION.MEDIA_MISSION_TYPE);
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.MEDIA_ACHIEVEMENT, 1);
    }
    return resp;
  }

  private ExtMessage processGetMediaInfo(Session session, long curMs) {
    session.userGameInfo.updateUserMedia(curMs);
    ExtMessage resp = ExtMessage.media();
    resp.msg = "ok";
    resp.data.gameInfo = session.userGameInfo;
    resp.serverTime = (int)(curMs/1000);
    return resp;
  }
}
