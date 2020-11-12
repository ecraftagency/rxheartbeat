package com.heartbeat.db.dao;

import com.heartbeat.scheduler.ExtendEventInfo;

import java.util.Map;

public class CommonEventDAO {
  public int                              lastSync;
  public Map<Integer, ExtendEventInfo>    events;

  public static CommonEventDAO of(Map<Integer, ExtendEventInfo> evtMap) {
    CommonEventDAO ced = new CommonEventDAO();
    ced.lastSync  = (int)(System.currentTimeMillis()/1000);
    ced.events    = evtMap;
    return ced;
  }
}