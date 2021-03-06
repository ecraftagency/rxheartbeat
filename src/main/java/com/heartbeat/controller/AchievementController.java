package com.heartbeat.controller;

import com.common.Constant;
import com.common.LOG;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.statics.VipData;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class AchievementController implements Handler<RoutingContext> {
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
          case "claimAchievement":
            resp = processClaimAchievement(session, ctx);
            break;
          case "getAchievements":
            resp = processGetAchievement(session);
            break;
          default:
            resp = ExtMessage.achievement();
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

  private ExtMessage processClaimAchievement(Session session, RoutingContext ctx) {
    int achievementType     = ctx.getBodyAsJson().getInteger("achievementType");
    int milestoneId         = ctx.getBodyAsJson().getInteger("milestoneId");
    ExtMessage resp         = ExtMessage.achievement();

    //start counting
    long totalTalent        = session.userIdol.totalCrt() + session.userIdol.totalPerf() + session.userIdol.totalAttr();
    VipData.VipDto vipData     = VipData.getVipData(session.userGameInfo.vipExp);
    session.userAchievement.setAchieveRecord(Constant.ACHIEVEMENT.TOTAL_TALENT_ACHIEVEMENT, totalTalent);
    session.userAchievement.setAchieveRecord(Constant.ACHIEVEMENT.LEVEL_ACHIEVEMENT, session.userGameInfo.titleId);
    session.userAchievement.setAchieveRecord(Constant.ACHIEVEMENT.VIP_ACHIEVEMENT, vipData.level);

    resp.msg                = session.userAchievement.claimAchievement(session, achievementType, milestoneId);
    resp.effectResults      = session.effectResults;
    resp.data.achievement   = session.userAchievement;

    return resp;
  }

  private ExtMessage processGetAchievement(Session session) {
    ExtMessage resp         = ExtMessage.achievement();
    //start counting
    long totalTalent        = session.userIdol.totalCrt() + session.userIdol.totalPerf() + session.userIdol.totalAttr();
    VipData.VipDto vipData  = VipData.getVipData(session.userGameInfo.vipExp);
    session.userAchievement.setAchieveRecord(Constant.ACHIEVEMENT.TOTAL_TALENT_ACHIEVEMENT, totalTalent);
    session.userAchievement.setAchieveRecord(Constant.ACHIEVEMENT.LEVEL_ACHIEVEMENT, session.userGameInfo.titleId);
    session.userAchievement.setAchieveRecord(Constant.ACHIEVEMENT.VIP_ACHIEVEMENT, vipData.level);

    resp.data.achievement   = session.userAchievement;
    return resp;
  }
}
