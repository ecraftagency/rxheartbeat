package com.heartbeat.event;

import com.heartbeat.common.Utilities;
import com.statics.EventInfo;

@SuppressWarnings("unused")
public class ExtEventInfo extends EventInfo {
  public static int FLUSH_DELAY = 86400;

  public static ExtEventInfo of(int id) {
    ExtEventInfo ei  = new ExtEventInfo();
    ei.eventId    = id;
    ei.strStart   = "";
    ei.strEnd     = "";
    ei.active     = false;
    ei.startTime  = -1;
    ei.endTime    = -1;
    ei.flushDelay = FLUSH_DELAY;
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
    }
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}