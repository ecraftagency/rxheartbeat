package com.heartbeat.controller;

import com.common.LOG;
import com.heartbeat.db.cb.CBTitle;
import com.heartbeat.effect.TitleEffectHandler;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import com.transport.model.Title;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class TitleController implements Handler<RoutingContext> {
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
          case "titleInfo":
            resp = processTitleInfo(ctx);
            break;
          default:
            resp = ExtMessage.title();
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

  private ExtMessage processTitleInfo(RoutingContext ctx) {
    int titleId     = ctx.getBodyAsJson().getInteger("titleId");
    String key      = TitleEffectHandler.titleKeyMap.get(titleId);
    String name     = TitleEffectHandler.titleNameMap.get(titleId);
    ExtMessage resp = ExtMessage.title();

    if (key == null || name == null) {
      resp.msg = "invalid_title_id";
    }

    Title title     = CBTitle.getInstance().load(key);
    if (title == null)
      title = Title.of("", "","", "");

    title.titleName = name;
    resp.data.title = title;
    resp.msg        = "ok";
    return resp;
  }
}
