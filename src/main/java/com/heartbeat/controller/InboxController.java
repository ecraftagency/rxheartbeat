package com.heartbeat.controller;

import com.common.Utilities;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserInbox;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InboxController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(InboxController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      int lastIssued    = ctx.user().principal().getInteger("issueTime");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null && session.userProfile.lastLogin == lastIssued) {
        ExtMessage resp;
        long curMs = System.currentTimeMillis();
        switch (cmd) {
          case "claimInboxReward":
            resp = processClaimInboxReward(session, ctx, curMs);
            break;
          case "getInboxMessage":
            resp = processGetInboxMessage(session, curMs);
            break;
          default:
            resp = ExtMessage.event();
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
      LOGGER.error(e.getCause().getMessage());
      ctx.response().setStatusCode(404).end();
    }
  }

  private ExtMessage processClaimInboxReward(Session session, RoutingContext ctx, long curMs) {
    ExtMessage resp     = ExtMessage.inbox();
    long msgId          = ctx.getBodyAsJson().getLong("messageId");
    resp.msg            = session.userInbox.claimInboxReward(session, msgId, curMs);
    resp.effectResults  = session.effectResults;
    resp.data.inbox     = session.userInbox;
    return resp;
  }

  private ExtMessage processGetInboxMessage(Session session, long curMs) {
    ExtMessage resp   = ExtMessage.inbox();
    resp.data.inbox   = session.userInbox;
    resp.data.extObj  = Utilities.gson.toJson(UserInbox.publicInbox);
    session.userInbox.lastMailCheckTime = curMs;
    return resp;
  }
}
