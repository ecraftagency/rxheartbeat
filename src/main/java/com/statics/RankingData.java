package com.statics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RankingData {
  public static class RewardDto implements Common.hasKey<Integer>{
    public int id;
    public int rankingType;
    public int rank;
    public List<List<Integer>> reward;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, RewardDto> rewardDtoMap;
  public static Map<Integer, Map<Integer, RewardDto>> rewardMap;

  public static void loadJson(String jsonText) {
    rewardDtoMap = Common.loadMap(jsonText, RewardDto.class);
    rewardMap = new HashMap<>();

    for (RankingData.RewardDto dto : rewardDtoMap.values()) {
      if (!rewardMap.containsKey(dto.rankingType)) {
        rewardMap.put(dto.rankingType, new TreeMap<>());
      }
      rewardMap.get(dto.rankingType).put(dto.rank, dto);
    }
  }
}