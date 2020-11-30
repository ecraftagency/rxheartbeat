package com.heartbeat.controller;

import com.common.LOG;
import com.common.Utilities;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.event.IdolEvent;
import com.heartbeat.event.RankingEvent;
import com.heartbeat.event.TimingEvent;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserEvent;
import com.heartbeat.scheduler.ExtendEventInfo;
import com.statics.GoldenTimeData;
import com.transport.ExtMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.*;

public class EventController implements Handler<RoutingContext> {
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
        long curMs = System.currentTimeMillis();
        switch (cmd) {
          case "claimUserEventReward":
            resp = processClaimUserEvtRwd(session, ctx, curMs);
            break;
          case "getUserEvents":
            resp = processGetUserEvt(session);
            break;
          case "getIdolEvents":
            resp = processGetIdolEvt();
            break;
          case "claimIdolEventReward":
            resp = processClaimIdolEvtRwd(session, ctx, curMs);
            break;
          case "getGoldenTimeInfo":
            resp = processGetGoldenTimeInfo(session, curMs);
            break;
          case "claimGoldenReward":
            resp = processClaimGoldenReward(session, curMs);
            break;
          case "getActiveEvents":
            resp = processGetActiveEvents(curMs);
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
      ctx.response().setStatusCode(404).end();
      LOG.globalException("node", cmd, e);
    }
  }

  private ExtMessage processGetActiveEvents(long curMs) {
    ExtMessage resp         = ExtMessage.event();

    Map<Integer, Set<Integer>> activeEvent = new HashMap<>();
    activeEvent.put(TimingEvent.EVENT_TYPE,   new HashSet<>());
    activeEvent.put(RankingEvent.EVENT_TYPE,  new HashSet<>());
    activeEvent.put(IdolEvent.EVENT_TYPE,     new HashSet<>());

    int curSec = (int)(curMs/1000);
    for (Map.Entry<Integer, ExtendEventInfo> entry : TimingEvent.evtMap.entrySet()) {
      ExtendEventInfo eei = entry.getValue();
      if (curSec > eei.startTime && curSec < (eei.endTime + eei.flushDelay))
        activeEvent.get(TimingEvent.EVENT_TYPE).add(eei.eventId);
    }

    for (Map.Entry<Integer, ExtendEventInfo> entry : RankingEvent.evtMap.entrySet()) {
      ExtendEventInfo eei = entry.getValue();
      if (curSec > eei.startTime && curSec < (eei.endTime + eei.flushDelay))
        activeEvent.get(RankingEvent.EVENT_TYPE).add(eei.eventId);

    }

    for (Map.Entry<Integer, ExtendEventInfo> entry : IdolEvent.evtMap.entrySet()) {
      ExtendEventInfo eei = entry.getValue();
      if (curSec > eei.startTime && curSec < (eei.endTime + eei.flushDelay))
        activeEvent.get(IdolEvent.EVENT_TYPE).add(eei.eventId);
    }

    resp.data.extObj        = Json.encode(activeEvent);
    resp.serverTime         = (int)(System.currentTimeMillis()/1000);
    return resp;
  }

  private ExtMessage processClaimGoldenReward(Session session, long curMs) {
    ExtMessage resp = ExtMessage.event();

    int goldenTimeId = UserEvent.getCurrentGoldenEvent(curMs);
    if (goldenTimeId > 0) {
      if (session.userEvent.goldenTimeClaimCas == goldenTimeId) { //already claim
        resp.msg = "Đã nhận phần thưởng tại khung giờ này";
      }
      else {
        resp.msg = "ok";
        session.userEvent.goldenTimeClaimCas = goldenTimeId;
        GoldenTimeData.GoldenTimeDto dto = GoldenTimeData.goldenTimeMap.get(goldenTimeId);
        if (dto == null || dto.reward == null) {
          resp.msg = "Thông tin sự kiện ko đúng";
        }
        else {
          for (List<Integer> reward : dto.reward)
          EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, reward);
        }
      }
    }
    else {
      session.userEvent.goldenTimeClaimCas = 0;
      resp.msg = "Sự kiện giờ vàng đã hết hiệu lực";
    }
    resp.data.event = session.userEvent;
    resp.effectResults = session.effectResults;
    return resp;
  }

  private ExtMessage processGetGoldenTimeInfo(Session session, long curMs) {
    ExtMessage resp = ExtMessage.event();

    int goldenTimeId = UserEvent.getCurrentGoldenEvent(curMs);
    resp.msg = "ok";
    resp.data.extObj = Utilities.gson.toJson(GoldenTimeData.goldenTimeMap);

    session.userEvent.currentGoldenEvent = goldenTimeId;
    resp.data.event = session.userEvent;
    return resp;
  }

  private ExtMessage processClaimIdolEvtRwd(Session session, RoutingContext ctx, long curMs) {
    ExtMessage resp     = ExtMessage.event();
    int idolId          = ctx.getBodyAsJson().getInteger("idolId");
    int evtId           = ctx.getBodyAsJson().getInteger("eventId");
    resp.msg            = session.userEvent.claimEventIdol(session, idolId, evtId, (int)(curMs/1000));
    resp.effectResults  = session.effectResults;
    resp.data.idols     = session.userIdol;
    resp.data.inventory = session.userInventory;
    return resp;
  }


  private ExtMessage processGetIdolEvt() {
    ExtMessage resp         = ExtMessage.event();
    resp.data.extObj        = Json.encode(IdolEvent.evtMap);
    resp.serverTime         = (int)(System.currentTimeMillis()/1000);
    return resp;
  }

  private ExtMessage processClaimUserEvtRwd(Session session, RoutingContext ctx, long curMs) {
    int eventType         = ctx.getBodyAsJson().getInteger("eventType");
    int milestoneId       = ctx.getBodyAsJson().getInteger("milestoneId");
    ExtMessage resp       = ExtMessage.event();
    resp.data.event       = session.userEvent;

    if (session.userGameInfo.isActiveTime()) {
      resp.msg            = session.userEvent.claimEventReward(session, eventType, milestoneId, (int)(curMs/1000));
      resp.effectResults  = session.effectResults;
    }

    return resp;
  }

  private ExtMessage processGetUserEvt(Session session) {
    session.userEvent.reBalance();
    ExtMessage resp         = ExtMessage.event();
    resp.data.event         = session.userEvent;
    resp.data.extObj        = Json.encode(TimingEvent.evtMap);
    resp.serverTime         = (int)(System.currentTimeMillis()/1000);
    return resp;
  }
}
