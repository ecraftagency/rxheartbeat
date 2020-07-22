package com.statics;

import java.util.List;
import java.util.Map;

public class DailyMissionData {
  public static class DailyMissionDTO implements Common.hasKey<Integer> {
    public int                  id;
    public int                  type;
    public String               desc;
    public int                  target;
    public List<List<Integer>>  rewards;
    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, DailyMissionDTO> missionMap;

  public static void loadJson(String jsonText) {
    missionMap = Common.loadMap(jsonText, DailyMissionDTO.class);
  }
}