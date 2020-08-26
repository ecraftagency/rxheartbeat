package com.heartbeat.event;

import com.common.Constant;
import com.common.Utilities;
import com.statics.IdolEventInfo;

import java.util.HashMap;

@SuppressWarnings("unused")
public class ExtIdolEventInfo extends IdolEventInfo {
  public static ExtIdolEventInfo of(int id) {
    ExtIdolEventInfo ei  = new ExtIdolEventInfo();
    ei.eventId    = id;
    ei.eventName  = "";
    ei.active     = true;
    ei.startTime  = -1;
    ei.endTime    = -1;
    ei.flushDelay = 0;
    ei.idolList   = new HashMap<>();
    return ei;
  }

  public ExtIdolEventInfo updateEventTime(String startDate, String endDate, int flushDelay) {
    try {
      int newStart     = (int)(Utilities.getMillisFromDateString(startDate, Constant.DATE_PATTERN)/1000);
      int newEnd       = (int)(Utilities.getMillisFromDateString(endDate, Constant.DATE_PATTERN)/1000);

      int second       = (int)(System.currentTimeMillis()/1000);
      if (second >= newStart)
        throw new IllegalArgumentException("[idol event] start time < current time");

      if (newEnd - newStart <= 0)
        throw new IllegalArgumentException("[idol event] end time < start time");

      startTime       = newStart;
      endTime         = newEnd;
      this.flushDelay = flushDelay > 0 ? flushDelay : FLUSH_DELAY;;
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

  public void addIdol(IdolClaimInfo icp) {
    if (icp != null)
      idolList.put(icp.idolId, icp);
  }
}