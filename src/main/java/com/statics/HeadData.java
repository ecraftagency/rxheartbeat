package com.statics;

import java.util.Map;

public class HeadData {
  public static class Head implements Common.hasKey<Integer> {
    public int id;
    public String name;
    public int gender;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, Head> headMap;

  public static void loadJson(String jsonText) {
    headMap = Common.loadMap(jsonText, Head.class);
  }
}