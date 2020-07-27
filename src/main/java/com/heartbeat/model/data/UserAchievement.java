package com.heartbeat.model.data;

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
    for (Integer achievementType : AchievementData.achieveMap.keySet())
      ua.records.put(achievementType, 0);
    ua.claimedAchievement = Arrays.asList(0L,0L,0L,0L,0L,0L,0L,0L,0L,0L);
    return ua;
  }

  public void recordAchievement(int achievementType, int amount) {
    if (!records.containsKey(achievementType))
      return;
    int oldVal = records.get(achievementType);
    records.put(achievementType, oldVal + amount);
  }

  public String claimAchievement(Session session, int achievementType, int milestoneId) {

    Map<Integer, AchievementData.AchievementDto> subMap = AchievementData.achieveMap.get(achievementType);
    if (subMap == null)
      return "achievement_not_found";

    AchievementData.AchievementDto dto = subMap.get(milestoneId);
    if (dto == null)
      return "milestone_not_found";

    if (!records.containsKey(achievementType))
      return "record_not_found";

    int currentVal = records.get(achievementType);
    if (currentVal < dto.milestoneValue)
      return "insufficient_record_count";

    if (!checkClaim(milestoneId))
      return "milestone_already_claim";

    List<List<Integer>> rewards = dto.reward;
    if (rewards == null)
      return "corrupt_reward";

    session.effectResults.clear();
    EffectHandler.ExtArgs extArgs = EffectHandler.ExtArgs.ofDefault(0, 0, "");
    for (List<Integer> reward : rewards )
      EffectManager.inst().handleEffect(extArgs, session, reward);
    recordClaim(milestoneId);
    return "ok";
  }
}