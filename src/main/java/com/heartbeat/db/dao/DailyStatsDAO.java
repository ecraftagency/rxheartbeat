package com.heartbeat.db.dao;

import java.util.HashMap;
import java.util.Map;

public class DailyStatsDAO {
  public Map<Integer, Integer>  dailyUseItem;
  public Map<Integer, Integer>  dailyGainItem;
  public Map<Integer, Integer>  dailyBacklogItem;

  public static DailyStatsDAO of(Map<Integer, Integer>  dailyUseItem,
                                 Map<Integer, Integer>  dailyGainItem,
                                 Map<Integer, Integer>  dailyBacklogItem) {
    DailyStatsDAO dao = new DailyStatsDAO();
    dao.dailyUseItem = new HashMap<>();
    dao.dailyUseItem.putAll(dailyUseItem);
    dao.dailyGainItem = new HashMap<>();
    dao.dailyGainItem.putAll(dailyGainItem);
    dao.dailyBacklogItem = new HashMap<>();
    dao.dailyBacklogItem.putAll(dailyBacklogItem);
    return dao;
  }
}
