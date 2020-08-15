package com.statics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ServantData {
  public static class Servant implements Common.hasKey<Integer> {
    public int servantID;
    public String name;
    public int specialityID;
    public int servantType;
    public int sort;
    public List<List<Integer>> books;
    public List<List<Integer>> unLockFormat;
    public List<List<Integer>> skills;
    public List<Integer> halo;
    public int haloNoUpdate;
    public List<List<Integer>> channels;
    public List<Integer> defaultProperties;

    @Override
    public Integer mapKey() {
      return servantID;
    }
  }

  public static Map<Integer, Servant> servantMap = new HashMap<>();

  public static void loadJson(String jsonString) {
    servantMap = Common.loadMap(jsonString, Servant.class);
  }
}