package com.heartbeat.event;

import com.common.Constant;
import com.common.Utilities;
import com.statics.EventInfo;

@SuppressWarnings("unused")
public class ExtEventInfo extends EventInfo {
  public static ExtEventInfo of(int id) {
    ExtEventInfo ei  = new ExtEventInfo();
    ei.eventId    = id;
    ei.active     = true;
    ei.startTime  = -1;
    ei.endTime    = -1;
    ei.flushDelay = FLUSH_DELAY;
    return ei;
  }

  public ExtEventInfo updateEventTime(String startDate, String endDate) {

    try {
      startTime  = (int)(Utilities.getMillisFromDateString(startDate, Constant.DATE_PATTERN)/1000);
      endTime    = (int)(Utilities.getMillisFromDateString(endDate, Constant.DATE_PATTERN)/1000);
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

    return this;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}