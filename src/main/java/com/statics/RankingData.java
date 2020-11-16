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
    public List<List<Integer>> reward1;
    public List<List<Integer>> reward2;
    public List<List<Integer>> reward3;
    public List<List<Integer>> reward4;

    @Override
    public Integer mapKey() {
      return id;
    }

    public List<List<Integer>> getRewardPack(int packId) {
      switch (packId){
        case 2:
          return reward2;
        case 3:
          return reward3;
        case 4:
          return reward4;
        default:
          return reward1;
      }
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