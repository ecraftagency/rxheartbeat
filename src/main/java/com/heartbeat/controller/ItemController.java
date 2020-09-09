package com.heartbeat.controller;

import com.common.LOG;
import com.common.Msg;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.statics.PropData;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class ItemController implements Handler<RoutingContext> {

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
          case "mergeItem":
            resp = processMergeItem(session, ctx);
            break;
          case "useSingleItem":
            resp = processUseEffectItem(session, ctx);
            break;
          case "useMultiItem":
            resp = processUserMultiItem(session, ctx);
            break;
          case "userInventory":
            resp = processGetUserInventory(session);
            break;
          default:
            resp = ExtMessage.item();
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

  private ExtMessage processMergeItem(Session session, RoutingContext ctx) {
    int mergeId         = ctx.getBodyAsJson().getInteger("mergeId");
    int mergeCount      = ctx.getBodyAsJson().getInteger("mergeCount");

    ExtMessage resp     = ExtMessage.item();
    resp.msg            = session.userInventory.mergeItem(session, mergeId, mergeCount);
    resp.data.inventory = session.userInventory.updateAndGet();
    resp.effectResults  = session.effectResults;
    return resp;
  }

  private ExtMessage processUseEffectItem(Session session, RoutingContext ctx) {
    ExtMessage  resp = ExtMessage.item();
    int         propId      = ctx.getBodyAsJson().getInteger("itemId");
    int         objId       = ctx.getBodyAsJson().getInteger("objId");
    int         intParam    = ctx.getBodyAsJson().getInteger("intParam"); //todo refactor this [objId, intParam, strParam]
    String      strParam    = ctx.getBodyAsJson().getString("strParam");
    int         amount      = 1;

    PropData.Prop prop = PropData.propMap.get(propId);
    if (prop == null || prop.isMultiUse != PropData.SINGLE_ITEM) {
      resp.msg = Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "invalid_item");
      return resp;
    }

    if (session.userInventory.useItem(propId, amount)) {
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.ofDefault(objId, intParam, strParam);
      resp.msg = EffectManager.inst().handleEffect(extArgs,session, prop.format);
      if (!resp.msg.equals("ok")) { //roll back
        session.userInventory.addItem(propId, amount);
      }
      resp.data.gameInfo  = session.userGameInfo;
      resp.data.inventory = session.userInventory.updateAndGet();
      resp.data.idols     = session.userIdol;
      resp.effectResults  = session.effectResults;
    }
    else {
      resp.msg = Msg.map.getOrDefault(Msg.INSUFFICIENT_ITEM, "insufficient_item");
    }

    return resp;
  }

  private ExtMessage processUserMultiItem(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.item();
    int propId      = ctx.getBodyAsJson().getInteger("itemId");
    int amount      = ctx.getBodyAsJson().getInteger("amount");
    int objId       = ctx.getBodyAsJson().getInteger("objId");

    PropData.Prop prop = PropData.propMap.get(propId);
    if (prop == null || prop.isMultiUse != PropData.MULTI_ITEM) {
      resp.msg = Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "invalid_item");
      return resp;
    }

    if (session.userInventory.haveItem(propId, amount)) {
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.ofDefault(objId, -1, "");
      session.userInventory.useItem(propId, amount);
      for (int i = 0; i < amount; i++) {
        resp.msg = EffectManager.inst().handleEffect(extArgs,session, prop.format);
      }
      resp.data.gameInfo    = session.userGameInfo;
      resp.data.inventory   = session.userInventory.updateAndGet();
      resp.data.idols       = session.userIdol;
      resp.effectResults    = session.effectResults;
    }
    else {
      resp.msg = Msg.map.getOrDefault(Msg.INSUFFICIENT_ITEM, "insufficient_item");
    }

    return resp;
  }

  private ExtMessage processGetUserInventory(Session session) {
    ExtMessage resp = ExtMessage.item();
    resp.data.gameInfo = session.userGameInfo;
    resp.data.inventory = session.userInventory.updateAndGet();
    resp.msg = "ok";
    return resp;
  }
}