package com.statics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementData {
  public static class AchievementDto implements Common.hasKey<Integer> {
    public int id;
    public int achievementType;
    public int milestoneId;
    public int milestoneValue;
    public String title;
    public String rewardDesc;
    public List<List<Integer>> reward;

    public int getType() {
      return achievementType;
    }

    @Override
    public Integer mapKey() {
      return id;
    }

  }

  public static Map<Integer, AchievementDto> achievementDtoMap;
  public static Map<Integer, Map<Integer, AchievementDto>> achieveMap;

  public static void loadJson(String jsonText) {
    achievementDtoMap = Common.loadMap(jsonText, AchievementDto.class);
    achieveMap = new HashMap<>();
    for (AchievementDto dto : achievementDtoMap.values()) {
      if (!achieveMap.containsKey(dto.achievementType)) {
        achieveMap.put(dto.achievementType, new HashMap<>());
      }
      achieveMap.get(dto.achievementType).put(dto.id, dto);
    }
  }
}
