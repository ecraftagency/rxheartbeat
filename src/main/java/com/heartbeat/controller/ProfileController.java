package com.heartbeat.controller;

import com.common.Constant;
import com.common.LOG;
import com.common.Utilities;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.statics.OfficeData;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import static com.common.Constant.*;

public class ProfileController implements Handler<RoutingContext> {
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
          case "buyShopItem":
            resp = processBuyShopItem(session, ctx);
            break;
          case "addVipExp":
            resp = processAddVipExp(session, ctx  );
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

  private ExtMessage processBuyShopItem(Session session, RoutingContext ctx) {
    int shopItemId      = ctx.getBodyAsJson().getInteger("shopItemId");
    ExtMessage resp     = ExtMessage.profile();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.SHOP_UNLOCK_LEVEL) {
      resp.msg = "level_limit";
      return resp;
    }

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
    if (session.userGameInfo.titleId == USER_GAME_INFO.TIME_ACTIVE_LEVEL) {
      session.userGameInfo.addTime(USER_GAME_INFO.INIT_TIME_GIFT);
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
      //ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      return resp;
    }

    int newGender           = ctx.getBodyAsJson().getInteger("gender");
    int newAvatar           = ctx.getBodyAsJson().getInteger("avatar");
    String newDisplayName   = ctx.getBodyAsJson().getString("displayName");


    if (newGender < 0 || newGender > USER_GAME_INFO.MAX_GENDER - 1) newGender = 0;
    if (newAvatar < 1 || newAvatar > USER_GAME_INFO.MAX_AVATAR) newAvatar = 1;

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
