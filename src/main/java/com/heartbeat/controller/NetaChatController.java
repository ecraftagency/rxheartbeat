package com.heartbeat.controller;

import com.common.LOG;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.netaChat.NetaAPI;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class NetaChatController implements Handler<RoutingContext> {
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
          case "getNetaGroup":
            resp = processGetNetaGroup();
            break;
          case "joinNetaGroup":
            resp = processJoinNetaGroup(ctx, cmd);
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

  private ExtMessage processJoinNetaGroup(RoutingContext ctx, String cmd) {
    ExtMessage resp   = ExtMessage.netalo();
    String groupId    = ctx.getBodyAsJson().getString("groupId");
    String netaUid    = ctx.getBodyAsJson().getString("userId");
    resp.cmd          = cmd;
    NetaAPI.joinGroup(groupId, netaUid);
    return resp;
  }

  private ExtMessage processGetNetaGroup() {
    ExtMessage resp   = ExtMessage.netalo();
    resp.data.netaGroup = NetaAPI.chatGroup;
    return resp;
  }
}