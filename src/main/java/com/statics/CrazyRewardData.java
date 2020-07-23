package com.statics;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CrazyRewardData {
  public static class CrazyReward implements Common.hasKey<Integer> {
    public int id;
    public int milestone;
    public List<List<Integer>> reward;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, CrazyReward> crazyRewardMap;

  public static void loadJson(String jsonText) {
    crazyRewardMap = Common.loadMap(jsonText, CrazyReward.class);
  }

  public static CrazyReward getCrazyReward(int crazyDegree) {
    int idx = 2;
    for (CrazyReward rw : crazyRewardMap.values()) {
      if (crazyDegree < rw.milestone) {
        return crazyRewardMap.get(idx-1);
      }
      idx++;
    }
    return ofNullObject();
  }

  public static CrazyReward ofNullObject() {
    CrazyReward cr = new CrazyReward();
    cr.id          = 5;
    cr.milestone   = 120;
    cr.reward      = Arrays.asList(Arrays.asList(11,2,2,3), Arrays.asList(102,49,0,0));
    return cr;
  }
}
