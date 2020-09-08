package com.heartbeat.model.data;

import com.common.Constant;
import com.common.Msg;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.heartbeat.scheduler.ExtendEventInfo;
import com.statics.EventData;
import com.statics.EventInfo;
import com.statics.ServantData;
import com.transport.EffectResult;
import com.transport.model.Event;
import com.transport.model.Idols;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.common.Constant.*;

public class UserEvent extends Event {
  public static UserEvent ofDefault() {
    UserEvent ue      = new UserEvent();
    ue.records        = new HashMap<>();
    ue.claimed        = new HashMap<>();
    ue.evt2cas        = new HashMap<>();

    for (Integer eventType : EventData.eventMap.keySet()) {
      ue.records.put(eventType, 0L);
      ue.claimed.put(eventType, Arrays.asList(0L,0L,0L,0L,0L,0L,0L,0L,0L,0L));
      ue.evt2cas.put(eventType, 0);
    }

    return ue;
  }

  public void reBalance() {
    for (Integer eventType : EventData.eventMap.keySet()) {
      records.putIfAbsent(eventType, 0L);
      claimed.putIfAbsent(eventType, Arrays.asList(0L,0L,0L,0L,0L,0L,0L,0L,0L,0L));
      evt2cas.putIfAbsent(eventType, 0);

      EventInfo ei = COMMON_EVENT.evtMap.get(eventType);
      if (ei != null && invalidCas(eventType, ei.startTime))
        resetEventData(eventType);
    }
  }

  private void resetEventData(int evtId) {
    records.computeIfPresent(evtId ,(k, v) -> v *= 0);
    claimed.computeIfPresent(evtId, (k, v) -> v = Arrays.asList(0L,0L,0L,0L,0L,0L,0L,0L,0L,0L));
    evt2cas.computeIfPresent(evtId, (k, v) -> v = 0); //reset cas to zero
  }

  private boolean invalidCas(int evtId, int cas) {
    int oldCas = evt2cas.getOrDefault(evtId, 0);
    return oldCas != cas;
  }

  public void addEventRecord(int eventType, long amount) {
    if (!EventData.eventMap.containsKey(eventType))
      return;

    EventInfo ei = COMMON_EVENT.evtMap.get(eventType);
    if (ei == null)
      return;

    if (invalidCas(eventType, ei.startTime)) {
      resetEventData(eventType);
    }

    int second    = (int)(System.currentTimeMillis()/1000);
    if (ei.startTime > 0 && ei.active && second >= ei.startTime && second <= ei.endTime) {
      long oldVal = records.getOrDefault(eventType, 0L);
      records.put(eventType, oldVal + amount);
      evt2cas.put(eventType, ei.startTime);
    }
  }

  public String claimEventReward(Session session, int eventType, int milestoneId, int second) {
    EventInfo ei = COMMON_EVENT.evtMap.get(eventType);
    if (ei == null)
      return Msg.msgMap.getOrDefault(Msg.EVENT_NOT_FOUND, "event_not_found");

    if (invalidCas(eventType, ei.startTime)) {
      return Msg.msgMap.getOrDefault(Msg.CAS_EXPIRE, "cas_expire");
    }

    if (ei.startTime <= 0 || !ei.active || second < ei.startTime || second > ei.endTime) {
      return Msg.msgMap.getOrDefault(Msg.TIMEOUT_CLAIM, "timeout_claim");
    }

    Map<Integer, EventData.EventDto> subMap = EventData.eventMap.get(eventType);
    if (subMap == null)
      return Msg.msgMap.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "event_not_found");

    EventData.EventDto dto = subMap.get(milestoneId);
    if (dto == null)
      return Msg.msgMap.getOrDefault(Msg.UNKNOWN_MILESTONE, "unknown_milestone");

    if (!records.containsKey(eventType))
      return Msg.msgMap.getOrDefault(Msg.RECORD_NOT_FOUND, "record_not_found");

    long currentVal = records.get(eventType);
    if (currentVal < dto.milestoneValue)
      return Msg.msgMap.getOrDefault(Msg.INSUFFICIENT_CLAIM, "insufficient_claim");

    if (checkClaim(eventType, milestoneId))
      return Msg.msgMap.getOrDefault(Msg.ALREADY_CLAIM, "already_claim");

    List<List<Integer>> rewards = dto.reward;
    if (rewards == null)
      return Msg.msgMap.getOrDefault(Msg.BLANK_REWARD, "blank_reward");

    session.effectResults.clear();
    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
    for (List<Integer> reward : rewards )
      EffectManager.inst().handleEffect(extArgs, session, reward);
    recordClaim(eventType, milestoneId);
    return "ok";
  }

  public String claimEventIdol(Session session, int idolId, int eventId, int second) {
    ExtendEventInfo ei  = IDOL_EVENT.evtMap.get(eventId);
    if (ei == null)
      return "event_not_found";

    if (ei.startTime <= 0 || !ei.active || second < ei.startTime || second > ei.endTime)
      return "event_time_out";

    EventInfo.IdolClaimInfo icp = ei.idolList.get(idolId);
    if (icp == null || ServantData.servantMap.get(icp.idolId) == null)
      return "idol_not_found";

    if (session.userIdol.idolMap.containsKey(idolId))
      return "idol_already_claim";

    if (!session.userInventory.haveItem(icp.requireItem, icp.amount))
      return "insufficient_item";

    session.userInventory.useItem(icp.requireItem, icp.amount);
    Idols.Idol idol = UserIdol.buildIdol(idolId);
    if (session.userIdol.addIdol(idol)) {
      session.effectResults.add(EffectResult.of(Constant.EFFECT_RESULT.IDOL_EFFECT_RESULT, idolId, 0));
      session.userAchievement.addAchieveRecord(Constant.ACHIEVEMENT.IDOL_ACHIEVEMENT, 1);
      return "ok";
    }
    else {
      return "unknown_err";
    }
  }
}