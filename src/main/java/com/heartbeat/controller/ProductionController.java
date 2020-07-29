package com.heartbeat.controller;

import com.heartbeat.common.Constant;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGroup;
import com.heartbeat.model.data.UserProduction;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class ProductionController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductionController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null) {
        long curMs = System.currentTimeMillis();
        ExtMessage resp;
        switch (cmd) {
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

  private ExtMessage addProductCount(Session session, long curMs, RoutingContext ctx) {
    int amount      = ctx.getBodyAsJson().getInteger("amount");
    int productType = ctx.getBodyAsJson().getInteger("productType");
    ExtMessage resp = ExtMessage.production();
    if (amount <= 0) {
      resp.msg = "wrong_amount";
      return resp;
    }

    if (!session.userInventory.haveItem(UserProduction.CLAIM_ITEM, amount)) {
      resp.msg = "insufficient_item";
      return resp;
    }

    session.userProduction.updateProduction(session, curMs);
    session.userInventory.useItem(UserProduction.CLAIM_ITEM, amount);
    session.userProduction.addProduction(productType, amount);
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
      if (Constant.GROUP.missionStart > 0) {
        UserGroup group = GroupPool.getGroupFromPool(session.groupID);
        if (group != null)
          group.addRecord(session, Constant.GROUP.missionStart, Constant.GROUP.PRODUCTION_MISSION_ID);
      }

      session.userDailyMission.addRecord(productType);

      switch (productType) {
        case UserProduction.PRODUCE_GOLD:
          session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.CRT_ACHIEVEMENT, 1);
          break;
        case UserProduction.PRODUCE_FAN:
          session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.FAN_ACHIEVEMENT, 1);
          break;
        case UserProduction.PRODUCE_VIEW:
          session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.VIEW_ACHIEVEMENT, 1);
          break;
        default:
          break;
      }
    }
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
