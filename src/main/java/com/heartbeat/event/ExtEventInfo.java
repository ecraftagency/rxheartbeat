package com.heartbeat.event;

import com.common.Constant;
import com.common.LOG;
import com.common.Utilities;
import com.statics.EventInfo;

@SuppressWarnings("unused")
public class ExtEventInfo extends EventInfo {
  public static ExtEventInfo of(int id) {
    ExtEventInfo ei  = new ExtEventInfo();
    ei.eventId    = id;
    ei.eventName  = "";
    ei.active     = true;
    ei.startTime  = -1;
    ei.endTime    = -1;
    ei.flushDelay = 0;
    return ei;
  }

  public ExtEventInfo updateEventTime(String startDate, String endDate, int flushDelay) {

    try {
      int newStart    = (int)(Utilities.getMillisFromDateString(startDate, Constant.DATE_PATTERN)/1000);
      int newEnd      = (int)(Utilities.getMillisFromDateString(endDate, Constant.DATE_PATTERN)/1000);

      int second      = (int)(System.currentTimeMillis()/1000);
      if (second >= newStart)
        throw new IllegalArgumentException("[user event] start time < current time");

      if (newEnd - newStart <= 0)
        throw new IllegalArgumentException("[user event] end time < start time");

      startTime       = newStart;
      endTime         = newEnd;
      this.flushDelay = flushDelay > 0 ? flushDelay*3600 : FLUSH_DELAY*3600;

    }
    catch (Exception e) {
      LOG.globalException(e);
    }

    return this;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}