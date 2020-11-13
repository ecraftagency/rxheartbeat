package com.heartbeat.db.dao;

import com.heartbeat.scheduler.ExtendEventInfo;
import com.transport.model.ScoreObj;

import java.util.List;
import java.util.Map;

public class RankingEventDAO {
  public int                              lastSync;
  public Map<Integer, ExtendEventInfo>    events;
  public Map<Integer, List<ScoreObj>>     evtRanking;

  public static RankingEventDAO of(Map<Integer, ExtendEventInfo> evtMap, Map<Integer, List<ScoreObj>> evtRanking) {
    RankingEventDAO dao = new RankingEventDAO();
    dao.lastSync        = (int)(System.currentTimeMillis()/1000);
    dao.events          = evtMap;
    dao.evtRanking      = evtRanking;
    return dao;
  }
}