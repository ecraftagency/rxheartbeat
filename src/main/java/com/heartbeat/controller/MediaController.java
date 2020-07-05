package com.heartbeat.controller;

import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserGameInfo;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class MediaController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MediaController.class);

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

  private ExtMessage processAddMediaCount(Session session, long curMs, RoutingContext ctx) {
    int amount = ctx.getBodyAsJson().getInteger("amount");
    ExtMessage resp = ExtMessage.media();
    if (amount <= 0) {
      resp.msg = "wrong_amount";
      return resp;
    }

    if (!session.userInventory.haveItem(UserGameInfo.CLAIM_MEDIA_COUNT_ITEM, amount)) {
      resp.msg = "insufficient_item";
      return resp;
    }

    session.userGameInfo.updateUserMedia(curMs);
    session.userInventory.useItem(UserGameInfo.CLAIM_MEDIA_COUNT_ITEM, amount);
    session.userGameInfo.addMediaClaim(amount);
    resp.data.gameInfo = session.userGameInfo;
    resp.msg = "ok";
    return resp;
  }

  private ExtMessage processClaimMedia(Session session, RoutingContext ctx) {
    int answer = ctx.getBodyAsJson().getInteger("answer");
    ExtMessage resp = ExtMessage.media();
    resp.msg = session.userGameInfo.claimMedia(session, answer);
    resp.data.gameInfo = session.userGameInfo;
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
