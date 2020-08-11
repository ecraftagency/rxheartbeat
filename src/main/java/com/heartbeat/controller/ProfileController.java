package com.heartbeat.controller;

import com.heartbeat.common.Constant;
import com.heartbeat.common.Utilities;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGameInfo;
import com.statics.OfficeData;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import static com.heartbeat.common.Constant.USER_GAME_INFO.INIT_TIME_GIFT;
import static com.heartbeat.common.Constant.USER_GAME_INFO.INIT_TIME_GIFT_LV;

public class ProfileController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null) {
        ExtMessage resp;
        switch (cmd) {
          case "buyShopItem":
            resp = processBuyShopItem(session, ctx);
            break;
          case "addVipExp":
            resp = processAddVipExp(session, ctx);
            break;
          case "userGameInfo":
            resp = processUserGameInfo(session);
            break;
          case "updateInfo": //async
            resp = processUpdateUserInfo(session, ctx);
            break;
          case "userLevelUp":
            resp = processUserLevelUp(session);
            break;
          default:
            resp = ExtMessage.profile();
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

  private ExtMessage processBuyShopItem(Session session, RoutingContext ctx) {
    int shopItemId      = ctx.getBodyAsJson().getInteger("shopItemId");
    ExtMessage resp     = ExtMessage.profile();
    resp.msg            = session.userGameInfo.buyShopItem(session, shopItemId);
    resp.data.gameInfo  = session.userGameInfo;
    resp.data.inventory = session.userInventory;
    resp.effectResults  = session.effectResults;

    if (resp.msg.equals("ok")) {
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.STORE_ACHIEVEMENT, 1);
    }

    return resp;
  }

  private ExtMessage processAddVipExp(Session session, RoutingContext ctx) {
    int amount          = ctx.getBodyAsJson().getInteger("amount");
    ExtMessage resp     = ExtMessage.profile();
    session.userGameInfo.addVipExp(session, amount);
    resp.data.gameInfo  = session.userGameInfo;
    return resp;
  }

  private ExtMessage processUserLevelUp(Session session) {
    ExtMessage resp = ExtMessage.profile();
    int currentTitle = session.userGameInfo.titleId;
    if (currentTitle == OfficeData.officeLV.size()) {
      resp.msg = "user_max_level";
      return resp;
    }

    OfficeData.OfficeLV nextLV = OfficeData.officeLV.get(currentTitle + 1);
    if (nextLV == null) {
      resp.msg = "user_level_invalid";
      return resp;
    }

    if (session.userGameInfo.exp < nextLV.exp) {
      resp.msg = "user_insufficient_exp";
      return resp;
    }

    session.userGameInfo.exp      -= nextLV.exp;
    session.userGameInfo.titleId   = nextLV.officeLV;
    session.userGameInfo.maxMedia++;
    if (session.userGameInfo.titleId == INIT_TIME_GIFT_LV) {
      session.userGameInfo.addTime(INIT_TIME_GIFT);
    }

    if (nextLV.rewardFormat != null && nextLV.rewardFormat.size() == 4)
      EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, nextLV.rewardFormat);

    resp.msg = "ok";
    resp.data.gameInfo    = session.userGameInfo;
    resp.data.production  = session.userProduction;
    resp.data.idols       = session.userIdol;
    resp.timeChange       = session.userGameInfo.timeChange;
    return resp;
  }

  private ExtMessage processUpdateUserInfo(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.profile();
    if (Utilities.isValidString(session.userGameInfo.displayName) ||
            session.userGameInfo.gender >= 0 ||
            session.userGameInfo.avatar >= 0) {
      resp.msg = "user_info_exist";
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
    }

    int newGender           = ctx.getBodyAsJson().getInteger("gender");
    int newAvatar           = ctx.getBodyAsJson().getInteger("avatar");
    String newDisplayName   = ctx.getBodyAsJson().getString("displayName");


    if (newGender < 0 || newGender > UserGameInfo.MAX_GENDER - 1) newGender = 0;
    if (newAvatar < 1 || newAvatar > UserGameInfo.MAX_AVATAR) newAvatar = 1;

    String result;
    try {
      if ((result = session.userGameInfo.updateDisplayName(session, newDisplayName)).equals("ok")) {
        session.userGameInfo.avatar = newAvatar;
        session.userGameInfo.gender = newGender;
      }
      else {
        session.userGameInfo.displayName = "";
      }

      session.userRanking.sessionId   = session.id;
      session.userRanking.displayName = session.userGameInfo.displayName;
    }
    catch (Exception e) {
      result = "username_invalid";
      resp.msg = result;
      return resp;
    }

    resp.msg = result;
    resp.data.gameInfo = session.userGameInfo;
    resp.data.idols = session.userIdol;
    resp.data.production = session.userProduction;
    return resp;
  }

  private ExtMessage processUserGameInfo(Session session) {
    ExtMessage resp       = ExtMessage.profile();
    resp.data.gameInfo    = session.userGameInfo;
    resp.data.production  = session.userProduction;
    resp.data.idols       = session.userIdol;
    resp.data.inventory   = session.userInventory;
    resp.data.fight       = session.userFight;
    return resp;
  }
}
