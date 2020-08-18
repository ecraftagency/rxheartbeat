package com.heartbeat.controller;

import com.common.LOG;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import static com.common.Constant.*;

public class RollCallController implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      int lastIssued    = ctx.user().principal().getInteger("issueTime");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null && session.userProfile.lastLogin == lastIssued) {
        ExtMessage resp;
        long curMs = System.currentTimeMillis();
        switch (cmd) {
          case "buyGiftCard":
            resp = processBuyGiftCard(session, curMs, ctx);
            break;
          case "claimGiftCardDaily":
            resp = processClaimGiftCardDaily(session, curMs, ctx);
            break;
          case "getRollCallInfo":
            resp = processGetRollCallInfo(session, curMs);
            break;
          case "claimDailyGift":
            resp = processClaimDailyGift(session, curMs);
            break;
          case "claimVipGift":
            resp = processClaimVipGift(session, curMs, ctx);
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
      LOG.globalException(e);
      ctx.response().setStatusCode(404).end();
    }
  }

  private ExtMessage processBuyGiftCard(Session session, long curMs, RoutingContext ctx) {
    int giftType        = ctx.getBodyAsJson().getInteger("giftType");
    ExtMessage resp     = ExtMessage.rollCall();
    resp.msg            = session.userRollCall.addGiftCard(session,curMs, giftType);
    resp.data.rollCall  = session.userRollCall;
    resp.effectResults  = session.effectResults;
    return resp;
  }

  private ExtMessage processClaimGiftCardDaily(Session session, long curMs, RoutingContext ctx) {
    int giftType        = ctx.getBodyAsJson().getInteger("giftType");
    ExtMessage resp     = ExtMessage.rollCall();
    resp.msg            = session.userRollCall.claimGiftCardDailyGift(session,curMs, giftType);
    resp.data.rollCall  = session.userRollCall;
    resp.effectResults  = session.effectResults;
    return resp;
  }

  private ExtMessage processClaimVipGift(Session session, long curMs, RoutingContext ctx) {
    int claimLevel      = ctx.getBodyAsJson().getInteger("claimLevel");
    ExtMessage resp     = ExtMessage.rollCall();
    resp.msg            = session.userRollCall.claimVipGift(session, curMs, claimLevel);
    resp.data.rollCall  = session.userRollCall;
    resp.effectResults  = session.effectResults;
    return resp;
  }

  private ExtMessage processClaimDailyGift(Session session, long curMs) {
    ExtMessage resp     = ExtMessage.rollCall();
    resp.msg            = session.userRollCall.claimDailyGift(session,curMs);
    resp.data.rollCall  = session.userRollCall;
    resp.effectResults  = session.effectResults;
    if (resp.msg.equals("ok")) {
      session.userAchievement.addAchieveRecord(ACHIEVEMENT.ROLL_CALL_ACHIEVEMENT, 1);
    }
    return resp;
  }

  private ExtMessage processGetRollCallInfo(Session session, long curMs) {
    ExtMessage resp     = ExtMessage.rollCall();
    resp.msg            = session.userRollCall.getRollCallInfo(session, curMs);
    resp.data.rollCall  = session.userRollCall;
    return resp;
  }
}
