package com.statics;

import java.util.*;

public class AchievementData {
  public static class AchievementDto implements Common.hasKey<Integer> {
    public int                  id;
    public int                  achievementType;
    public int                  milestoneId;
    public long                 milestoneValue;
    public String               title;
    public String               rewardDesc;
    public List<List<Integer>>  reward;

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
        achieveMap.put(dto.achievementType, new TreeMap<>());
      }
      achieveMap.get(dto.achievementType).put(dto.milestoneId, dto);
    }
  }

  public static List<AchievementDto> getAchievementDto(int achievementType, int milestoneVal) throws NoSuchElementException {
    Map<Integer, AchievementDto> subMap = achieveMap.get(achievementType);
    if (subMap == null)
      return Arrays.asList(null, null);

    int lastID = 0;
    for (AchievementDto dto : subMap.values()) {
      if (milestoneVal < dto.milestoneValue) {
        return Arrays.asList(subMap.get(dto.milestoneId - 1), subMap.get(dto.milestoneId));
      }
      lastID = dto.milestoneId;
    }
    return Arrays.asList(subMap.get(lastID), null);
  }
}
