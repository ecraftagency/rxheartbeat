package com.statics;

import java.util.List;
import java.util.Map;

public class ServantHonorData {
  public static class ServantHonor implements Common.hasKey<Integer> {
    public int                  honorID;
    public String               name;
    public int                  maxServantLV;
    public int                  maxBookLV;
    public List<List<Integer>>  needFormat;
    public List<Integer>        rewardFormat;

    @Override
    public Integer mapKey() {
      return honorID;
    }
  }

  public static Map<Integer, ServantHonor> honorMap;

  public static void loadJson(String jsonString) {
    honorMap = Common.loadMap(jsonString, ServantHonor.class);
  }
}