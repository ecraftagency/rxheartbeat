package com.statics;

import java.util.List;
import java.util.Map;

public class HaloBaseData {
  public static class HaloBase implements Common.hasKey<Integer> {
    public int haloID;
    public String name;
    public int imageID;
    public int lv;
    public List<Integer> preFixHalo;
    public int haloType;
    public int fOrUHalo;

    @Override
    public Integer mapKey() {
      return haloID;
    }
  }

  public static Map<Integer, HaloBase> haloMap;

  public static void loadJson(String jsonText) {
    haloMap = Common.loadMap(jsonText, HaloBase.class);
  }
}
