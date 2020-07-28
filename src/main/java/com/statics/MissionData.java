package com.statics;

import java.util.List;
import java.util.Map;

public class MissionData {
  public static class MissionDto implements Common.hasKey<Integer> {
    public int id;
    public String name;
    public String desc;
    public List<Integer> queryFormat;
    public List<List<Integer>> rewardFormat;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, MissionDto> missionDtoMap;

  public static void loadJson(String jsonText) {
    missionDtoMap = Common.loadMap(jsonText, MissionDto.class);
  }
}