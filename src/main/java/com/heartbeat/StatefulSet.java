package com.heartbeat;

import com.common.Constant;
import com.common.LOG;
import com.heartbeat.db.cb.AbstractCruder;
import com.heartbeat.db.dao.CommonEventDAO;
import com.heartbeat.db.dao.RankingEventDAO;
import com.heartbeat.model.data.UserRanking;
import com.heartbeat.ranking.impl.LeaderBoard;
import com.heartbeat.scheduler.ExtendEventInfo;
import com.transport.model.ScoreObj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.common.Constant.RANK_EVENT.*;


import static com.heartbeat.HBServer.rxIndexBucket;

public class StatefulSet {
  private static AbstractCruder<CommonEventDAO>   cbCommonEvtAccess;
  private static AbstractCruder<RankingEventDAO>  cbRankEvtAccess;
  private static final String                     commonEvtKey  = "serverEvent";
  private static final String                     rankEvtKey    = "rankEvent";
  private static final String                     idolEvtKey    = "idolEvent";

  static {
    cbCommonEvtAccess = new AbstractCruder<>(CommonEventDAO.class, rxIndexBucket);
    cbRankEvtAccess   = new AbstractCruder<>(RankingEventDAO.class, rxIndexBucket);
  }

  public static void loadIdolEvtFromDB() {
    try {
      CommonEventDAO dao = cbCommonEvtAccess.load(idolEvtKey);
      if (dao != null && dao.events != null)
        for (ExtendEventInfo eei : dao.events.values())
          Constant.IDOL_EVENT.evtMap.computeIfPresent(eei.eventId, (k,v) -> v = eei);
    }
    catch (Exception e) {
      LOG.globalException("Node", "loadCommonEvtFromDB", e);
    }
  }

  public static void syncIdolEventToDB() {
    try {
      CommonEventDAO dao = CommonEventDAO.of(Constant.IDOL_EVENT.evtMap);
      cbCommonEvtAccess.sync(idolEvtKey, dao);
    }
    catch (Exception e) {
      LOG.globalException("Node", "syncIdolEventToDB", e);
    }
  }

  public static void loadCommonEvtFromDB() {
    try {
      CommonEventDAO dao = cbCommonEvtAccess.load(commonEvtKey);
      if (dao != null && dao.events != null)
        for (ExtendEventInfo eei : dao.events.values())
          Constant.COMMON_EVENT.evtMap.computeIfPresent(eei.eventId, (k,v) -> v = eei);
    }
    catch (Exception e) {
      LOG.globalException("Node", "loadCommonEvtFromDB", e);
    }
  }

  public static void syncCommonEvtToDB() {
    try {
      CommonEventDAO dao = CommonEventDAO.of(Constant.COMMON_EVENT.evtMap);
      cbCommonEvtAccess.sync(commonEvtKey, dao);
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
            evtMap.computeIfPresent(eei.eventId, (k,v) -> {
              UserRanking.rankings.put(eei.eventId, new LeaderBoard<>(LDB_CAPACITY, dao.evtRanking.get(eei.eventId), recordLock));
              return eei;
            });
          }
        }
      }
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

      RankingEventDAO dao = RankingEventDAO.of(evtMap, rankArchive);
      cbRankEvtAccess.sync(rankEvtKey, dao);
    }
    catch (Exception e) {
      LOG.globalException("Node", "syncTankEvtToDB", e);
    }
  }
}