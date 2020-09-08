package com.heartbeat.model.data;

import com.common.Msg;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.AchievementData;
import com.transport.model.Achievement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAchievement extends Achievement {
  public static UserAchievement ofDefault() {
    UserAchievement ua    = new UserAchievement();
    ua.records            = new HashMap<>();
    ua.claimedAchievement = new HashMap<>();
    for (Integer achievementType : AchievementData.achieveMap.keySet()) {
      ua.records.put(achievementType, 0L);
      ua.claimedAchievement.put(achievementType, Arrays.asList(0L,0L,0L,0L,0L,0L,0L,0L,0L,0L));
    }
    return ua;
  }

  public void addAchieveRecord(int achievementType, long amount) {
    if (!records.containsKey(achievementType))
      records.put(achievementType, 0L);
    long oldVal = records.get(achievementType);
    records.put(achievementType, oldVal + amount);
  }

  public void setAchieveRecord(int achievementType, long value) {
    records.put(achievementType, value);
  }

  public String claimAchievement(Session session, int achievementType, int milestoneId) {

    Map<Integer, AchievementData.AchievementDto> subMap = AchievementData.achieveMap.get(achievementType);
    if (subMap == null)
      return Msg.msgMap.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "achievement_not_found");

    AchievementData.AchievementDto dto = subMap.get(milestoneId);
    if (dto == null)
      return Msg.msgMap.getOrDefault(Msg.UNKNOWN_MILESTONE, "unknown_milestone");

    if (!records.containsKey(achievementType))
      return Msg.msgMap.getOrDefault(Msg.RECORD_NOT_FOUND, "record_not_found");

    long currentVal = records.get(achievementType);
    if (currentVal < dto.milestoneValue)
      return Msg.msgMap.getOrDefault(Msg.INSUFFICIENT_CLAIM, "insufficient_record_count");

    if (checkClaim(achievementType, milestoneId))
      return Msg.msgMap.getOrDefault(Msg.ALREADY_CLAIM, "milestone_already_claim");

    List<List<Integer>> rewards = dto.reward;
    if (rewards == null)
      return Msg.msgMap.getOrDefault(Msg.BLANK_REWARD, "blank_reward");

    session.effectResults.clear();
    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.of();
    for (List<Integer> reward : rewards )
      EffectManager.inst().handleEffect(extArgs, session, reward);
    recordClaim(achievementType, milestoneId);
    return "ok";
  }
}