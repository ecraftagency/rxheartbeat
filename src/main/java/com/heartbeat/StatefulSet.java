package com.heartbeat;

import com.common.LOG;
import com.heartbeat.db.cb.AbstractCruder;
import com.heartbeat.db.dao.IdolEventDAO;
import com.heartbeat.db.dao.TimingEventDAO;
import com.heartbeat.db.dao.RankingEventDAO;
import com.heartbeat.event.IdolEvent;
import com.heartbeat.event.RankingEvent;
import com.heartbeat.event.TimingEvent;
import com.heartbeat.model.data.UserRanking;
import com.heartbeat.ranking.impl.LeaderBoard;
import com.heartbeat.scheduler.ExtendEventInfo;
import com.transport.model.ScoreObj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.heartbeat.HBServer.rxIndexBucket;

public class StatefulSet {
  private static AbstractCruder<TimingEventDAO>   cbTimingEvtAccess;
  private static AbstractCruder<RankingEventDAO>  cbRankEvtAccess;
  private static AbstractCruder<IdolEventDAO>     cbIdolEvtAccess;

  private static final String                     commonEvtKey  = "serverEvent";
  private static final String                     rankEvtKey    = "rankEvent";
  private static final String                     idolEvtKey    = "idolEvent";

  static {
    cbTimingEvtAccess = new AbstractCruder<>(TimingEventDAO.class, rxIndexBucket);
    cbRankEvtAccess   = new AbstractCruder<>(RankingEventDAO.class, rxIndexBucket);
    cbIdolEvtAccess   = new AbstractCruder<>(IdolEventDAO.class, rxIndexBucket);
  }

  public static void loadIdolEvtFromDB() {
    try {
      IdolEventDAO dao = cbIdolEvtAccess.load(idolEvtKey);
      if (dao != null && dao.events != null)
        for (ExtendEventInfo eei : dao.events.values())
          IdolEvent.evtMap.computeIfPresent(eei.eventId, (k, v) -> v = eei);

      if (dao != null && dao.eventPlan != null)
        IdolEvent.evtPlan.putAll(dao.eventPlan);
    }
    catch (Exception e) {
      LOG.globalException("Node", "loadCommonEvtFromDB", e);
    }
  }

  public static void syncIdolEventToDB() {
    try {
      IdolEventDAO dao = IdolEventDAO.of(IdolEvent.evtMap, IdolEvent.evtPlan);
      cbIdolEvtAccess.sync(idolEvtKey, dao);
    }
    catch (Exception e) {
      LOG.globalException("Node", "syncIdolEventToDB", e);
    }
  }

  public static void loadTimingEvtFromDB() {
    try {
      TimingEventDAO dao = cbTimingEvtAccess.load(commonEvtKey);
      if (dao != null && dao.events != null)
        for (ExtendEventInfo eei : dao.events.values())
          TimingEvent.evtMap.computeIfPresent(eei.eventId, (k, v) -> v = eei);
      if (dao != null && dao.eventPlan != null)
        TimingEvent.evtPlan.putAll(dao.eventPlan);
    }
    catch (Exception e) {
      LOG.globalException("Node", "loadCommonEvtFromDB", e);
    }
  }

  public static void syncTimingEvtToDB() {
    try {
      TimingEventDAO dao = TimingEventDAO.of(TimingEvent.evtMap, TimingEvent.evtPlan);
      cbTimingEvtAccess.sync(commonEvtKey, dao);
    }
    catch (Exception e) {
      LOG.globalException("Node", "syncCommonEvtToDB", e);
    }
  }

  public static void loadRankEvtFromDB() {
    try {
      RankingEventDAO dao = cbRankEvtAccess.load(rankEvtKey);
      int curMs = (int)(System.currentTimeMillis()/1000);
      if (dao != null && dao.events != null && dao.evtRanking != null) {
        for (ExtendEventInfo eei : dao.events.values()) {
          if (dao.evtRanking.containsKey(eei.eventId)) {
            boolean recordLock = curMs >= eei.endTime;
            RankingEvent.evtMap.computeIfPresent(eei.eventId, (k, v) -> {
              UserRanking.rankings.put(eei.eventId, new LeaderBoard<>(RankingEvent.LDB_CAPACITY, dao.evtRanking.get(eei.eventId), recordLock));
              return eei;
            });
          }
        }
      }

      if (dao != null && dao.eventPlan != null)
        RankingEvent.evtPlan.putAll(dao.eventPlan);
    }
    catch (Exception e) {
      LOG.globalException("Node", "loadRankEvtFromDB", e);
    }
  }

  public static void syncRankEvtToDB() {
    try {
      Map<Integer, List<ScoreObj>> rankArchive = new HashMap<>();
      for (Map.Entry<Integer, LeaderBoard<Integer, ScoreObj>> entry : UserRanking.rankings.entrySet()) {
        rankArchive.put(entry.getKey(), entry.getValue().get());
      }

      RankingEventDAO dao = RankingEventDAO.of(RankingEvent.evtMap, rankArchive, RankingEvent.evtPlan);
      cbRankEvtAccess.sync(rankEvtKey, dao);
    }
    catch (Exception e) {
      LOG.globalException("Node", "syncTankEvtToDB", e);
    }
  }
}