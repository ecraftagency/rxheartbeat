package com.statics;

import java.util.HashMap;
import java.util.Map;

public class SpecialityData {
  public static class Speciality implements Common.hasKey<Integer> {
    public int specialtyID;
    public String name;
    @Override
    public Integer mapKey() {
      return specialtyID;
    }
  }
  public static Map<Integer, Speciality> specialities = new HashMap<>();

  public static void loadJson(String jsonText) {
    specialities = Common.loadMap(jsonText, Speciality.class);
  }
}
