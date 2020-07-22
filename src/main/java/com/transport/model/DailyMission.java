package com.transport.model;

import java.util.Map;

public class DailyMission {
  public static class Mission {
    public int id;
    public int dailyCount;
    public boolean claim;

    public static Mission of(int id, int count) {
      Mission res = new Mission();
      res.id = id;
      res.dailyCount = count;
      res.claim = false;
      return res;
    }
  }

  public Map<Integer, Mission> missionMap;
}