package com.statics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfficeData{
  public static class OfficeLV implements Common.hasKey<Integer> {
    public int officeLV;
    public String name;
    public int exp;
    public int unLockServantID;
    public int moneyMaxTimes;
    public int foodMaxTimes;
    public int soldierMaxTimes;
    public int affairsMaxTimes;
    public List<Integer> salaryFormat;
    public int demonSpanMinutes;
    public int imperialSpanMinutes;
    public int areaDemonSpanMinutes;
    public int areaImperialSpanMinutes;

    @Override
    public Integer mapKey() {
      return officeLV;
    }
  }

  public static Map<Integer, OfficeLV> officeLV = new HashMap<>();

  public static void loadJson(String jsonText) {
    officeLV = Common.loadMap(jsonText, OfficeLV.class);
  }
}
