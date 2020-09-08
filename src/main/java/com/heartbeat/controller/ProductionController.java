package com.heartbeat.controller;

import com.common.Constant;
import com.common.LOG;
import com.common.Msg;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGroup;
import com.heartbeat.model.data.UserProduction;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class ProductionController implements Handler<RoutingContext> {
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
          case "claimMultiProduct":
            resp = processClaimMultiProduct(session);
            break;
          case "productInfo":
            resp = processGetProductInfo(session, curMs);
            break;
          case "claimProduct": //async
            resp = processClaimProduct(session, ctx);
            break;
          case "addProductCount":
            resp = addProductCount(session, curMs, ctx);
            break;
          default:
            resp = ExtMessage.production();
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

  private ExtMessage processClaimMultiProduct(Session session) {
    int goldCntBef = session.userProduction.currentGoldClaimCount;
    int viewCntBef = session.userProduction.currentViewClaimCount;
    int fanCntBef  = session.userProduction.currentFanClaimCount;

    ExtMessage resp       = ExtMessage.production();
    resp.msg              = session.userProduction.multiProduce(session);
    resp.data.gameInfo    = session.userGameInfo;
    resp.data.production  = session.userProduction;
    resp.data.idols       = session.userIdol;

    if (resp.msg.equals("ok")) {
      int deltaGold = goldCntBef - session.userProduction.currentGoldClaimCount;
      int deltaView = viewCntBef - session.userProduction.currentViewClaimCount;
      int deltaFan  = fanCntBef  - session.userProduction.currentFanClaimCount;

      if (deltaGold > 0) {
        UserGroup group = GroupPool.getGroupFromPool(session.groupID);
        if (group != null)
          group.addRecord(session, Constant.GROUP_EVENT.GE_PROD_EVT_ID, deltaGold, true);

        session.userDailyMission.addRecord(UserProduction.PRODUCE_GOLD, deltaGold);
        session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.CRT_ACHIEVEMENT, deltaGold);
        session.userEvent.addEventRecord(Constant.COMMON_EVENT.CRT_PROD_EVT_ID, deltaGold);
      }
      if (deltaView > 0) {
        session.userDailyMission.addRecord(UserProduction.PRODUCE_VIEW, deltaView);
        session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.VIEW_ACHIEVEMENT, deltaView);
        session.userEvent.addEventRecord(Constant.COMMON_EVENT.VIEW_PROD_EVT_ID, deltaView);
      }
      if (deltaFan > 0) {
        session.userDailyMission.addRecord(UserProduction.PRODUCE_FAN, deltaFan);
        session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.FAN_ACHIEVEMENT, deltaFan);
        session.userEvent.addEventRecord(Constant.COMMON_EVENT.FAN_PROD_EVT_ID, deltaFan);
      }
    }

    resp.effectResults = session.effectResults;
    return resp;
  }

  private ExtMessage addProductCount(Session session, long curMs, RoutingContext ctx) {
    int amount      = ctx.getBodyAsJson().getInteger("amount");
    int productType = ctx.getBodyAsJson().getInteger("productType");
    ExtMessage resp = ExtMessage.production();
    if (amount <= 0) {
      resp.msg = Msg.map.getOrDefault(Msg.MALFORM_ARGS, "wrong_amount");
      return resp;
    }

    if (!session.userInventory.haveItem(UserProduction.CLAIM_ITEM, amount)) {
      resp.msg = Msg.map.getOrDefault(Msg.INSUFFICIENT_ITEM, "insufficient_item");
      return resp;
    }

    session.userProduction.updateProduction(session, curMs);
    session.userInventory.useItem(UserProduction.CLAIM_ITEM, amount);
    session.userProduction.addProduction(session, productType, amount);
    resp.data.production = session.userProduction;
    resp.msg = "ok";
    return resp;
  }

  private ExtMessage processClaimProduct(Session session, RoutingContext ctx) {
    int productType       = ctx.getBodyAsJson().getInteger("productType");
    String result         = session.userProduction.produce(session, productType);
    ExtMessage resp       = ExtMessage.production();
    resp.msg              = result;
    resp.data.gameInfo    = session.userGameInfo;
    resp.data.production  = session.userProduction;
    resp.data.idols       = session.userIdol;

    if (resp.msg.equals("ok")) {

      UserGroup group = GroupPool.getGroupFromPool(session.groupID);
      if (group != null && productType == UserProduction.PRODUCE_GOLD)
        group.addRecord(session, Constant.GROUP_EVENT.GE_PROD_EVT_ID, 1, true);

      session.userDailyMission.addRecord(productType);

      switch (productType) {
        case UserProduction.PRODUCE_GOLD:
          session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.CRT_ACHIEVEMENT, 1);
          session.userEvent.addEventRecord(Constant.COMMON_EVENT.CRT_PROD_EVT_ID, 1);
          break;
        case UserProduction.PRODUCE_FAN:
          session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.FAN_ACHIEVEMENT, 1);
          session.userEvent.addEventRecord(Constant.COMMON_EVENT.FAN_PROD_EVT_ID, 1);
          break;
        case UserProduction.PRODUCE_VIEW:
          session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.VIEW_ACHIEVEMENT, 1);
          session.userEvent.addEventRecord(Constant.COMMON_EVENT.VIEW_PROD_EVT_ID, 1);
          break;
        default:
          break;
      }
    }

    resp.effectResults = session.effectResults;
    return resp;
  }

  private ExtMessage processGetProductInfo(Session session, long curMs) {
    session.userProduction.updateProduction(session, curMs);
    ExtMessage resp       = ExtMessage.production();
    resp.msg              = "ok";
    resp.data.production  = session.userProduction;
    resp.data.gameInfo    = session.userGameInfo;
    resp.data.idols       = session.userIdol;
    resp.serverTime       = (int)(curMs/1000);
    return resp;
  }
}
