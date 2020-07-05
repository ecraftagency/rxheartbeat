package com.statics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropData {
  public static final int MATERIAL_ITEM = 0;
  public static final int SINGLE_ITEM   = 1;
  public static final int MULTI_ITEM    = 2;

  public static class Prop implements Common.hasKey<Integer> {
    public int            propID;
    public List<Integer>  format;
    public String         name;
    public int            sort;
    public String         effect;
    public int            status;
    public int            isExpire;
    public int            expireSeconds;
    public int            propVer;
    public int            isMultiUse;
    public String         approach;

    @Override
    public Integer mapKey() {
      return propID;
    }
  }

  public static Map<Integer, Prop> propMap = new HashMap<>();

  public static void loadJson(String jsonText) {
    propMap = Common.loadMap(jsonText, Prop.class);
  }
}
