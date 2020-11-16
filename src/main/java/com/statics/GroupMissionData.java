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
    public List<List<Integer>>  reward3;
    public List<List<Integer>>  reward4;

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

  public static Map<Integer, GroupMission> missionMap;

  public static void loadJson(String jsonText) {
    missionMap = Common.loadMap(jsonText, GroupMission.class);
  }
}