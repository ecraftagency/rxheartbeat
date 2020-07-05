package com.statics;

import java.util.HashMap;
import java.util.Map;

public class ServantLVData {
  public static class ServantLV implements Common.hasKey<Integer>{
    public int lv;
    public long exp;

    @Override
    public Integer mapKey() {
      return lv;
    }
  }

  public static Map<Integer, ServantLV> servantLV = new HashMap<>();

  public static void loadJson(String jsonText) {
    servantLV = Common.loadMap(jsonText, ServantLV.class);
  }
}
