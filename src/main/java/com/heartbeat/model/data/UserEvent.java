package com.heartbeat.model.data;

import com.heartbeat.common.Constant;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.event.ExtIdolEventInfo;
import com.heartbeat.model.Session;
import com.statics.EventData;
import com.statics.EventInfo;
import com.statics.IdolEventInfo;
import com.statics.ServantData;
import com.transport.EffectResult;
import com.transport.model.Event;
import com.transport.model.Idols;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.heartbeat.common.Constant.*;

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

      EventInfo ei = Constant.USER_EVENT.evtMap.get(eventType);
      if (ei != null && invalidCas(eventType, ei.startTime))
        resetEventData(eventType);
    }
  }

  private void resetEventData(int eventType) {
    records.computeIfPresent(eventType ,(k, v) -> v *= 0);
    claimed.computeIfPresent(eventType, (k, v) -> v = Arrays.asList(0L,0L,0L,0L,0L,0L,0L,0L,0L,0L));
    evt2cas.computeIfPresent(eventType, (k, v) -> v = 0); //reset cas to zero
  }

  private boolean invalidCas(int eventType, int cas) {
    int oldCas = evt2cas.getOrDefault(eventType, 0);
    return oldCas != cas;
  }

  public void addEventRecord(int eventType, long amount) {
    if (!EventData.eventMap.containsKey(eventType))
      return;

    EventInfo ei = Constant.USER_EVENT.evtMap.get(eventType);
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
    EventInfo ei = Constant.USER_EVENT.evtMap.get(eventType);
    if (ei == null)
      return "event_not_found";

    if (invalidCas(eventType, ei.startTime)) {
      return "cas_expire";
    }

    if (ei.startTime <= 0 || !ei.active || second < ei.startTime || second > ei.endTime) {
      return "event_time_out";
    }

    Map<Integer, EventData.EventDto> subMap = EventData.eventMap.get(eventType);
    if (subMap == null)
      return "event_not_found";

    EventData.EventDto dto = subMap.get(milestoneId);
    if (dto == null)
      return "milestone_not_found";

    if (!records.containsKey(eventType))
      return "record_not_found";

    long currentVal = records.get(eventType);
    if (currentVal < dto.milestoneValue)
      return "insufficient_record_count";

    if (checkClaim(eventType, milestoneId))
      return "milestone_already_claim";

    List<List<Integer>> rewards = dto.reward;
    if (rewards == null)
      return "corrupt_reward";

    session.effectResults.clear();
    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
    for (List<Integer> reward : rewards )
      EffectManager.inst().handleEffect(extArgs, session, reward);
    recordClaim(eventType, milestoneId);
    return "ok";
  }

  public String claimEventIdol(Session session, int idolId, int eventId, int second) {
    ExtIdolEventInfo ei  = IDOL_EVENT.evtMap.get(eventId);
    if (ei == null)
      return "event_not_found";

    if (ei.startTime <= 0 || !ei.active || second < ei.startTime || second > ei.endTime)
      return "event_time_out";

    IdolEventInfo.IdolClaimInfo icp = ei.idolList.get(idolId);
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