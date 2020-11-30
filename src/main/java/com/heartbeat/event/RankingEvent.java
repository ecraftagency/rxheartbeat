package com.heartbeat.event;

import com.common.LOG;
import com.heartbeat.scheduler.ExtendEventInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RankingEvent {
  public static final int         EVENT_TYPE                = 2;
  public static final int         TOTAL_TALENT_RANK_ID      = 13;
  public static final int         FIGHT_RANK_ID             = 14;
  public static final int         MONEY_SPEND_RANK_ID       = 15;
  public static final int         VIEW_SPEND_RANK_ID        = 16;
  public static final int         FAN_SPEND_RANK_ID         = 17;
  public static final int         LDB_CAPACITY              = 100;
  public static final Map<Integer, ExtendEventInfo>         evtMap;
  public static final ConcurrentHashMap<Integer, List<ExtendEventInfo>> evtPlan;

  public static final Set<Integer> eventSet;
  static {
    eventSet    = new HashSet<>();
    evtMap      = new HashMap<>();
    evtMap.put(VIEW_SPEND_RANK_ID,    ExtendEventInfo.of(VIEW_SPEND_RANK_ID, 1));
    evtMap.put(FAN_SPEND_RANK_ID,     ExtendEventInfo.of(FAN_SPEND_RANK_ID, 1));
    evtMap.put(TOTAL_TALENT_RANK_ID,  ExtendEventInfo.of(TOTAL_TALENT_RANK_ID, 1));
    evtMap.put(FIGHT_RANK_ID,         ExtendEventInfo.of(FIGHT_RANK_ID, 1));
    evtMap.put(MONEY_SPEND_RANK_ID,   ExtendEventInfo.of(MONEY_SPEND_RANK_ID, 1));

    eventSet.add(VIEW_SPEND_RANK_ID);
    eventSet.add(FAN_SPEND_RANK_ID);
    eventSet.add(TOTAL_TALENT_RANK_ID);
    eventSet.add(FIGHT_RANK_ID);
    eventSet.add(MONEY_SPEND_RANK_ID);

    evtPlan = new ConcurrentHashMap<>();
  }

  public static void update() {
    synchronized (Common.class) {
      try {
        int curSec = (int)(System.currentTimeMillis()/1000);
        Enumeration<Integer> e = evtPlan.keys();
        while (e.hasMoreElements()) {
          Integer eventId = e.nextElement();
          List<ExtendEventInfo> events = evtPlan.get(eventId);
          if (events != null) {
            for (ExtendEventInfo eei : events) {
              ExtendEventInfo curEvt = evtMap.get(eei.eventId);
              if (curEvt != null && !curEvt.equals(eei) && (curSec >= eei.startTime && curSec <= (eei.endTime + eei.flushDelay))) {
                evtMap.put(eei.eventId, eei);
                break;
              }
            }
          }
        }
      }
      catch(Exception ex) {
        LOG.poolException(ex);
      }
    }
  }
}