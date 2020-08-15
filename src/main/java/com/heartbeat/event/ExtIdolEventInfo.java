package com.heartbeat.event;

import com.heartbeat.common.Utilities;
import com.statics.IdolEventInfo;

import java.util.HashMap;

@SuppressWarnings("unused")
public class ExtIdolEventInfo extends IdolEventInfo {
  public static int FLUSH_DELAY = 86400;

  public static ExtIdolEventInfo of(int id, String name) {
    ExtIdolEventInfo ei  = new ExtIdolEventInfo();
    ei.eventId    = id;
    ei.eventName  = name;
    ei.strStart   = "";
    ei.strEnd     = "";
    ei.active     = false;
    ei.startTime  = -1;
    ei.endTime    = -1;
    ei.flushDelay = FLUSH_DELAY;
    ei.idolList   = new HashMap<>();
    return ei;
  }

  public void updateEventTime(String startDate, String endDate) {
    strStart  = startDate;
    strEnd    = endDate;

    try {
      startTime  = (int)(Utilities.getMillisFromDateString(strStart, DATE_PATTERN)/1000);
      endTime    = (int)(Utilities.getMillisFromDateString(strEnd, DATE_PATTERN)/1000);
      flushDelay = FLUSH_DELAY;

      int second    = (int)(System.currentTimeMillis()/1000);
      if (second >= startTime)
        throw new IllegalArgumentException("event time < current time");

      if (endTime - startTime <= 0)
        throw new IllegalArgumentException("end time < start time");
    }
    catch (Exception e) {
      startTime  = -1;
      endTime    = -1;
      idolList.clear();
    }
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void addIdol(IdolClaimInfo icp) {
    if (icp != null)
      idolList.put(icp.idolId, icp);
  }
}