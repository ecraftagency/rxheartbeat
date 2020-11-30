package com.heartbeat.event;

import com.common.LOG;
import com.heartbeat.scheduler.ExtendEventInfo;
import com.statics.EventInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IdolEvent {
  public static final int         EVENT_TYPE                = 3;
  public static final int         BP_EVT_ID                 = 0;
  public static final int         DB_EVT_ID                 = 1;
  public static final int         NT_EVT_ID                 = 2;

  public static final Map<Integer, ExtendEventInfo>                     evtMap;
  public static final ConcurrentHashMap<Integer, List<ExtendEventInfo>> evtPlan;
  public static final Map<Integer, List<EventInfo.IdolClaimInfo>>       evtIdols;

  static {
    evtMap    = new HashMap<>();
    evtIdols  = new HashMap<>();

    ExtendEventInfo blackPinkEvt  = ExtendEventInfo.of(BP_EVT_ID, 1);
    blackPinkEvt.eventName        = "Black Pink";

    ExtendEventInfo banDamEvt     = ExtendEventInfo.of(DB_EVT_ID, 1);
    banDamEvt.eventName           = "Ban Dam";

    ExtendEventInfo ngocTrinhEvt = ExtendEventInfo.of(NT_EVT_ID, 1);
    banDamEvt.eventName           = "Ban Dam";

    evtMap.put(BP_EVT_ID, blackPinkEvt);
    evtMap.put(DB_EVT_ID, banDamEvt);
    evtMap.put(NT_EVT_ID, ngocTrinhEvt);

    evtIdols.put(BP_EVT_ID, Arrays.asList(
      EventInfo.IdolClaimInfo.of(48, 93, 10),
      EventInfo.IdolClaimInfo.of(49, 93, 10),
      EventInfo.IdolClaimInfo.of(50, 93, 10),
      EventInfo.IdolClaimInfo.of(51, 93, 10),
      EventInfo.IdolClaimInfo.of(52, 93, 10)
    ));

    evtIdols.put(DB_EVT_ID, Arrays.asList(
      EventInfo.IdolClaimInfo.of(43, 92, 10),
      EventInfo.IdolClaimInfo.of(44, 92, 10),
      EventInfo.IdolClaimInfo.of(45, 92, 10),
      EventInfo.IdolClaimInfo.of(46, 92, 10)
    ));

    evtIdols.put(NT_EVT_ID, Collections.singletonList(EventInfo.IdolClaimInfo.of(53, 140, 10)));

    for (Map.Entry<Integer, List<EventInfo.IdolClaimInfo>> entry : evtIdols.entrySet()) {
      List<EventInfo.IdolClaimInfo> idols = entry.getValue();
      for (EventInfo.IdolClaimInfo idol : idols)
        evtMap.get(entry.getKey()).addIdol(idol);
    }

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
                List<EventInfo.IdolClaimInfo> idols = evtIdols.get(eei.eventId);
                if (idols != null) {
                  for (EventInfo.IdolClaimInfo idol : idols)
                    eei.addIdol(idol);
                  evtMap.put(eei.eventId, eei);
                  break;
                }
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
