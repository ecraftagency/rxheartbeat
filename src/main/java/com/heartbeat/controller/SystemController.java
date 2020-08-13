package com.heartbeat.controller;

import com.heartbeat.common.Constant;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserInbox;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class SystemController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd        = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null) {
        long curMs = System.currentTimeMillis();
        ExtMessage resp;
        if (cmd.equals("heartbeat")) {
          resp = processHeartBeat(session, curMs);
        }
        else {
          resp = ExtMessage.system();
          resp.msg = "unknown_cmd";
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

  private ExtMessage processHeartBeat(Session session, long curMs) {
    ExtMessage resp = ExtMessage.system();
    int second      = (int)(curMs/1000);
    int deltaTime   = second - session.lastHearBeatTime;

    //todo delta time is always >= real time consume, but just let it be
    long remainTime = session.userGameInfo.remainTime();
    long timeSpent = deltaTime > remainTime ? remainTime : deltaTime;
    session.userEvent.addEventRecord(Constant.EVENT.TIME_SPEND_EVT_ID, timeSpent);

    session.userGameInfo.subtractTime(deltaTime);

    session.updateOnline(curMs);
    resp.userRemainTime = session.userGameInfo.remainTime();

    //check new inbox message
    long cas = UserInbox.checkNewMessage(session.userInbox.lastMailCheckTime);
    resp.newInbox = cas > 0;

    resp.serverTime = second;
    return resp;
  }
}