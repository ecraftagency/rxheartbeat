package com.heartbeat.controller;

import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MissionController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(MissionController.class);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String cmd = ctx.getBodyAsJson().getString("cmd");
      String strUserId = ctx.user().principal().getString("username");
      Session session = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null) {
        ExtMessage resp;
        switch (cmd) {
          case "missionInfo":
            resp = processMissionInfo(session);
            break;
          case "unlockMission":
            resp = processUnlockMission(session);
            break;
          default:
            resp = ExtMessage.mission();
            resp.msg = "unknown_cmd";
            break;
        }

        resp.cmd            = cmd;
        resp.timeChange     = session.userGameInfo.timeChange;
        resp.userRemainTime = session.userGameInfo.remainTime();

        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        session.effectResults.clear();
        session.userGameInfo.timeChange = false;
      } else {
        ctx.response().setStatusCode(401).end();
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      ctx.response().setStatusCode(404).end();
    }
  }

  private ExtMessage processUnlockMission(Session session) {
    ExtMessage resp     = ExtMessage.mission();
    resp.msg            = session.userMission.unlockMission(session);
    resp.data.mission   = session.userMission;
    resp.effectResults  = session.effectResults;
    return resp;
  }

  private ExtMessage processMissionInfo(Session session) {
    ExtMessage resp   = ExtMessage.mission();
    session.userMission.updateAccomplishment(session);
    resp.data.mission = session.userMission;
    return resp;
  }
}