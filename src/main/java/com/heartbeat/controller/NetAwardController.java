package com.heartbeat.controller;

import com.common.LOG;
import com.common.Msg;
import com.heartbeat.db.cb.CBNetAward;
import com.heartbeat.effect.NetAwardEffectHandler;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import com.transport.model.NetAward;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class NetAwardController implements Handler<RoutingContext> {
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
          case "netAwardInfo":
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
    String key      = NetAwardEffectHandler.titleKeyMap.get(titleId);
    String name     = NetAwardEffectHandler.titleNameMap.get(titleId);
    ExtMessage resp = ExtMessage.title();

    if (key == null || name == null) {
      resp.msg = Msg.map.getOrDefault(Msg.MALFORM_ARGS, "invalid_title_id");
    }

    NetAward title     = CBNetAward.getInstance().load(key);
    if (title == null)
      title = NetAward.of("", "","", "");

    title.titleName = name;
    resp.data.netAward = title;
    resp.msg        = "ok";
    return resp;
  }
}
