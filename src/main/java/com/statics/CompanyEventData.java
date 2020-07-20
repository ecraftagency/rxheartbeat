package com.statics;

import java.util.List;
import java.util.Map;

public class CompanyEventData {
  public static class CompanyEvent implements Common.hasKey<Integer>{
    public int            id;
    public int            hitMember;
    public int            hitCount;
    public List<Integer>  gift;
    public String         name;
    public String         desc;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, CompanyEvent> eventMap;

  public static void loadJson(String jsonText) {
    eventMap = Common.loadMap(jsonText, CompanyEvent.class);
  }
}