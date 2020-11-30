package com.heartbeat.db.dao;

import com.heartbeat.scheduler.ExtendEventInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdolEventDAO {
  public int                                  lastSync;
  public Map<Integer, ExtendEventInfo>        events;
  public Map<Integer, List<ExtendEventInfo>>  eventPlan;

  public static IdolEventDAO of(Map<Integer, ExtendEventInfo> evtMap, Map<Integer, List<ExtendEventInfo>> eventPlan) {
    IdolEventDAO ced = new IdolEventDAO();
    ced.lastSync  = (int)(System.currentTimeMillis()/1000);
    ced.events    = evtMap;
    ced.eventPlan = new HashMap<>();
    ced.eventPlan.putAll(eventPlan);
    return ced;
  }
}
