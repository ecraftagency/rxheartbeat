package com.heartbeat.controller;

import com.common.Constant;
import com.common.LOG;
import com.common.Msg;
import com.common.Utilities;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGroup;
import com.heartbeat.service.GroupService;
import com.heartbeat.service.impl.GroupServiceV1;
import com.statics.OfficeData;
import com.statics.ShopData;
import com.transport.ExtMessage;
import com.transport.model.CompactProfile;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

import static com.common.Constant.*;

public class ProfileController implements Handler<RoutingContext> {
  private GroupService groupService;
  public ProfileController() {
    groupService = new GroupServiceV1();
  }
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
          case "getShopStatus":
            resp = processGetShopStatus();
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
            case "getCompactProfile":
            processCompactProfile(ctx, cmd);
            return;
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

  private void processCompactProfile(RoutingContext ctx, String cmd) {
    ExtMessage resp = ExtMessage.profile();
    resp.cmd        = cmd;
    int userId      = ctx.getBodyAsJson().getInteger("userId");

    //first get online user [level 1 cache]
    Session session = SessionPool.getSessionFromPool(userId);
    if (session != null) {
      transformUserProfile(userId, session, resp, ctx);
      return;
    }

    //if not get from std_profile service [level 2 cache]
    CompactProfile profile = Session.getProfileFromCache(userId);
    if (profile != null) {
      resp.data.extObj    = Utilities.gson.toJson(profile);
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      return;
    }

    //last from persistent
    CBSession.getInstance().load(Integer.toString(userId), ar -> {
      if (ar.succeeded()) {
        transformUserProfile(userId, ar.result(), resp, ctx);
      }
      else {
        resp.msg = ar.cause().getMessage();
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
    });
  }

  private void transformUserProfile(int userId, Session session, ExtMessage resp, RoutingContext ctx) {
    groupService.loadSessionGroup(session, ar -> {
      if (ar.succeeded()) {
        CompactProfile profile = new CompactProfile();
        UserGroup group = GroupPool.getGroupFromPool(session.groupID);
        String groupName = group != null ? group.name : "";
        resp.msg = "ok";
        profile.displayName = session.userGameInfo.displayName;
        profile.titleId     = session.userGameInfo.titleId;
        profile.vipExp      = session.userGameInfo.vipExp;
        profile.totalCrt    = session.userIdol.totalCrt();
        profile.totalAttr   = session.userIdol.totalAttr();
        profile.totalPerf   = session.userIdol.totalPerf();
        profile.groupName   = groupName;
        profile.userId      = userId;
        profile.avatar      = session.userGameInfo.avatar;
        profile.gender      = session.userGameInfo.gender;
        profile.exp         = session.userGameInfo.exp;
        profile.curFightLV  = session.userFight.currentFightLV;
        resp.data.extObj    = Utilities.gson.toJson(profile);
      }
      else {
        resp.msg = ar.cause().getMessage();
      }
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
    });
  }

  private ExtMessage processGetShopStatus() {
    ExtMessage resp = ExtMessage.profile();
    Map<Integer, Integer> shopStatus = new HashMap<>();
    for (ShopData.ShopDto dto : ShopData.shopDtoMap.values()) {
      shopStatus.put(dto.id, dto.status);
    }
    resp.data.extObj = Json.encode(shopStatus);
    return resp;
  }

  private ExtMessage processBuyShopItem(Session session, RoutingContext ctx) {
    int shopItemId      = ctx.getBodyAsJson().getInteger("shopItemId");
    ExtMessage resp     = ExtMessage.profile();

    if (session.userGameInfo.titleId < UNLOCK_FUNCTION.SHOP_UNLOCK_LEVEL) {
      resp.msg = Msg.map.getOrDefault(Msg.LEVEL_LIMIT, "level_limit");
      return resp;
    }

    resp.msg            = session.userGameInfo.buyShopItem(session, shopItemId);
    resp.data.gameInfo  = session.userGameInfo;
    resp.data.inventory = session.userInventory.updateAndGet();
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
      resp.msg = Msg.map.getOrDefault(Msg.USER_MAX_LEVEL, "user_max_level");
      return resp;
    }

    OfficeData.OfficeLV nextLV = OfficeData.officeLV.get(currentTitle + 1);
    if (nextLV == null) {
      resp.msg = Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "user_level_invalid");
      return resp;
    }

    if (session.userGameInfo.exp < nextLV.exp) {
      resp.msg = Msg.map.getOrDefault(Msg.USER_EXP_INSUFFICIENT, "user_insufficient_exp");
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
    resp.effectResults    = session.effectResults;
    return resp;
  }

  private ExtMessage processUpdateUserInfo(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.profile();
    if (Utilities.isValidString(session.userGameInfo.displayName) ||
            session.userGameInfo.gender >= 0 ||
            session.userGameInfo.avatar >= 0) {
      resp.msg = Msg.map.getOrDefault(Msg.USER_INFO_EXIST, "user_info_exist");
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
      result = Msg.map.getOrDefault(Msg.INVALID_DISPLAY_NAME, "invalid_username");
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
    resp.data.inventory   = session.userInventory.updateAndGet();
    resp.data.fight       = session.userFight;
    return resp;
  }
}
