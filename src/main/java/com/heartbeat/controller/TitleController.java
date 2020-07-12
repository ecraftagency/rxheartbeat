package com.heartbeat.controller;

import com.heartbeat.db.impl.CBBadge;
import com.heartbeat.effect.TitleEffectHandler;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import com.transport.model.Title;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitleController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MediaController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));
      if (session != null) {
        ExtMessage resp;
        switch (cmd) {
          case "titleInfo":
            resp = processTitleInfo(session, ctx);
            break;
          default:
            resp = ExtMessage.title();
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

  private ExtMessage processTitleInfo(Session session, RoutingContext ctx) {
    int titleId     = ctx.getBodyAsJson().getInteger("titleId");
    String key      = TitleEffectHandler.titleMap.get(titleId);
    ExtMessage resp = ExtMessage.title();
    if (key == null) {
      resp.msg = "invalid_title_id";
    }

    Title title     = CBBadge.getInstance().load(key);
    if (title == null)
      title = Title.of("", "", "");

    resp.data.title = title;
    resp.msg        = "ok";
    return resp;
  }
}
