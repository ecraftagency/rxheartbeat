package com.heartbeat.controller;

import com.common.LOG;
import com.heartbeat.event.TimingEvent;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserInbox;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class SystemController implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    String cmd          = "";
    try {
      cmd               = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      int lastIssued    = ctx.user().principal().getInteger("issueTime");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null && session.userProfile.lastLogin == lastIssued) {
        long curMs = System.currentTimeMillis();
        ExtMessage resp;
        switch (cmd) {
          case "heartbeat":
            resp = processHeartBeat(session, curMs);
            break;
          case "sleep":
            resp = ExtMessage.system();
            resp.group = "none";
            session.sleep = true;
            break;
          default:
            resp = ExtMessage.system();
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
      ctx.response().setStatusCode(404).end();
      LOG.globalException("node", cmd, e);
    }
  }

  private ExtMessage processHeartBeat(Session session, long curMs) {
    ExtMessage resp = ExtMessage.system();
    int second      = (int)(curMs/1000);
    int deltaTime   = second - session.lastHearBeatTime;

    //todo delta time is always >= real time consume, but just let it be
    long remainTime = session.userGameInfo.remainTime();
    long timeSpent = deltaTime > remainTime ? remainTime : deltaTime;
    session.userEvent.addEventRecord(TimingEvent.TIME_SPEND_EVT_ID, timeSpent);

    session.userGameInfo.subtractTime(deltaTime); //todo money money money ^^!

    session.updateOnline(curMs);
    session.sleep = false;
    resp.userRemainTime = session.userGameInfo.remainTime();

    //check new inbox message
    long lastPublicMsgTime = UserInbox.checkNewMessage(session.userInbox.lastMailCheckTime);
    resp.newInbox = lastPublicMsgTime > 0 || session.userInbox.haveNewPriMsg();

    resp.serverTime = second;

    return resp;
  }
}