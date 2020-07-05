package com.heartbeat.controller;


import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.statics.PropData;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class ItemController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));
      if (session != null) {
        ExtMessage resp;
        switch (cmd) {
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
        resp.cmd = cmd;
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
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

  private ExtMessage processUseEffectItem(Session session, RoutingContext ctx) {
    ExtMessage resp = ExtMessage.item();
    int propId      = ctx.getBodyAsJson().getInteger("itemId");
    int objId       = ctx.getBodyAsJson().getInteger("objId");
    int newAvatar   = ctx.getBodyAsJson().getInteger("newAvatar");
    String newDN    = ctx.getBodyAsJson().getString("newDisplayName");
    int amount      = 1;

    PropData.Prop prop = PropData.propMap.get(propId);
    if (prop == null || prop.isMultiUse != PropData.SINGLE_ITEM) {
      resp.msg = "invalid_item";
      return resp;
    }

    if (session.userInventory.useItem(propId, amount)) {
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of(objId, newAvatar, newDN);
      resp.msg = EffectManager.inst().handleEffect(extArgs,session, prop.format);
      if (!resp.msg.equals("ok")) { //roll back
        session.userInventory.addItem(propId, amount);
      }
      resp.data.gameInfo = session.userGameInfo;
      resp.data.inventory = session.userInventory;
    }
    else {
      resp.msg = "not_enough_item";
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
      resp.msg = "invalid_item";
      return resp;
    }

    if (session.userInventory.haveItem(propId, amount)) {
      EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of(objId, -1, "");
      session.userInventory.useItem(propId, amount);
      for (int i = 0; i < amount; i++) {
        resp.msg = EffectManager.inst().handleEffect(extArgs,session, prop.format);
      }
      resp.data.gameInfo = session.userGameInfo;
      resp.data.inventory = session.userInventory;
    }
    else {
      resp.msg = "not_enough_item";
    }

    return resp;
  }

  private ExtMessage processGetUserInventory(Session session) {
    ExtMessage resp = ExtMessage.item();
    resp.data.gameInfo = session.userGameInfo;
    resp.data.inventory = session.userInventory;
    resp.msg = "ok";
    return resp;
  }
}