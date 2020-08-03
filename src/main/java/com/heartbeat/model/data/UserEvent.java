package com.heartbeat.model.data;

import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.EventData;
import com.transport.model.Event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserEvent extends Event {
  public static UserEvent ofDefault() {
    UserEvent ue      = new UserEvent();
    ue.records        = new HashMap<>();
    ue.claimed        = new HashMap<>();

    for (Integer eventType : EventData.eventMap.keySet()) {
      ue.records.put(eventType, 0L);
      ue.claimed.put(eventType, Arrays.asList(0L,0L,0L,0L,0L,0L,0L,0L,0L,0L));
    }

    return ue;
  }

  public void addEventRecord(int eventType, long amount) {
    if (!records.containsKey(eventType))
      records.put(eventType, 0L);
    long oldVal = records.get(eventType);
    records.put(eventType, oldVal + amount);
  }

  public void setEventRecord(int eventType, long value) {
    records.put(eventType, value);
  }

  public String claimEventReward(Session session, int eventType, int milestoneId) {

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
}