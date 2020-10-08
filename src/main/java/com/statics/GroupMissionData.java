package com.statics;

import java.util.List;
import java.util.Map;

public class GroupMissionData {
  public static class GroupMission implements Common.hasKey<Integer>{
    public int                  id;
    public int                  hitMember;
    public int                  hitCount;
    public String               name;
    public String               desc;
    public List<List<Integer>>  reward1;
    public List<List<Integer>>  reward2;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, GroupMission> missionMap;

  public static void loadJson(String jsonText) {
    missionMap = Common.loadMap(jsonText, GroupMission.class);
  }
}