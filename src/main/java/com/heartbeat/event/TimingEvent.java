package com.heartbeat.event;

import com.common.LOG;
import com.heartbeat.scheduler.ExtendEventInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TimingEvent {
  public static final int         EVENT_TYPE                = 1;
  public static final int         TIME_SPEND_EVT_ID         = 21;
  public static final int         APT_BUFF_USE_EVT_ID       = 6700;
  public static final int         MONEY_SPEND_EVT_ID        = 6;
  public static final int         VIEW_SPEND_EVT_ID         = 7;
  public static final int         FAN_SPEND_EVT_ID          = 8;
  public static final int         CRT_PROD_EVT_ID           = 9;
  public static final int         VIEW_PROD_EVT_ID          = 10;
  public static final int         FAN_PROD_EVT_ID           = 11;
  public static final int         GAME_SHOW_EVT_ID          = 12;
  public static final int         TOTAL_TALENT_EVT_ID       = 13;
  public static final int         VIP_INCR_EVT_ID           = 14;

  public static final Map<Integer, ExtendEventInfo>                     evtMap;
  public static final ConcurrentHashMap<Integer, List<ExtendEventInfo>> evtPlan;
  static {
    evtMap = new HashMap<>();
    evtMap.put(TIME_SPEND_EVT_ID,   ExtendEventInfo.of(TIME_SPEND_EVT_ID, 1));
    evtMap.put(APT_BUFF_USE_EVT_ID, ExtendEventInfo.of(APT_BUFF_USE_EVT_ID, 1));
    evtMap.put(MONEY_SPEND_EVT_ID,  ExtendEventInfo.of(MONEY_SPEND_EVT_ID, 1));
    evtMap.put(VIEW_SPEND_EVT_ID,   ExtendEventInfo.of(VIEW_SPEND_EVT_ID, 1));
    evtMap.put(FAN_SPEND_EVT_ID,    ExtendEventInfo.of(FAN_SPEND_EVT_ID, 1));
    evtMap.put(CRT_PROD_EVT_ID,     ExtendEventInfo.of(CRT_PROD_EVT_ID, 1));
    evtMap.put(VIEW_PROD_EVT_ID,    ExtendEventInfo.of(VIEW_PROD_EVT_ID, 1));
    evtMap.put(FAN_PROD_EVT_ID,     ExtendEventInfo.of(FAN_PROD_EVT_ID, 1));
    evtMap.put(TOTAL_TALENT_EVT_ID, ExtendEventInfo.of(TOTAL_TALENT_EVT_ID, 1));
    evtMap.put(GAME_SHOW_EVT_ID,    ExtendEventInfo.of(GAME_SHOW_EVT_ID, 1));
    evtMap.put(VIP_INCR_EVT_ID,     ExtendEventInfo.of(VIP_INCR_EVT_ID, 1));

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