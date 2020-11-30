package com.heartbeat.db.dao;

import com.heartbeat.scheduler.ExtendEventInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimingEventDAO {
  public int                                  lastSync;
  public Map<Integer, ExtendEventInfo>        events;
  public Map<Integer, List<ExtendEventInfo>>  eventPlan;

  public static TimingEventDAO of(Map<Integer, ExtendEventInfo> evtMap, Map<Integer, List<ExtendEventInfo>> eventPlan) {
    TimingEventDAO ced = new TimingEventDAO();
    ced.lastSync  = (int)(System.currentTimeMillis()/1000);
    ced.events    = evtMap;
    ced.eventPlan = new HashMap<>();
    ced.eventPlan.putAll(eventPlan);
    return ced;
  }
}