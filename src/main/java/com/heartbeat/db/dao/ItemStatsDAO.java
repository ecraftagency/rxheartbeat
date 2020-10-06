package com.heartbeat.db.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemStatsDAO {
  public int lastSync;
  public Map<Integer, Integer> itemCnt;

  public static ItemStatsDAO ofDefault() {
    ItemStatsDAO dao  = new ItemStatsDAO();
    dao.lastSync      = 0 ;
    dao.itemCnt       = new ConcurrentHashMap<>();
    return dao;
  }

  public static ItemStatsDAO of(Map<Integer, Integer> cntData) {
    ItemStatsDAO dao  = new ItemStatsDAO();
    dao.itemCnt       = cntData;
    dao.lastSync      = (int)(System.currentTimeMillis()/1000);
    return dao;
  }
}
