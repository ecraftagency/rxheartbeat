package com.heartbeat.db.dao;

import com.heartbeat.scheduler.ExtendEventInfo;
import com.transport.model.ScoreObj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingEventDAO {
  public int                                  lastSync;
  public Map<Integer, ExtendEventInfo>        events;
  public Map<Integer, List<ScoreObj>>         evtRanking;
  public Map<Integer, List<ExtendEventInfo>>  eventPlan;

  public static RankingEventDAO of(Map<Integer, ExtendEventInfo> evtMap, Map<Integer, List<ScoreObj>> evtRanking, Map<Integer, List<ExtendEventInfo>> eventPlan) {
    RankingEventDAO dao = new RankingEventDAO();
    dao.lastSync        = (int)(System.currentTimeMillis()/1000);
    dao.events          = evtMap;
    dao.evtRanking      = evtRanking;
    dao.eventPlan       = new HashMap<>();
    dao.eventPlan.putAll(eventPlan);
    return dao;
  }
}