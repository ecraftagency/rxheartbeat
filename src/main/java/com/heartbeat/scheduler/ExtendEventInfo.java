package com.heartbeat.scheduler;

import com.common.Constant;
import com.common.LOG;
import com.common.Utilities;
import com.statics.EventInfo;

import java.util.HashMap;

public class ExtendEventInfo extends EventInfo implements TaskRunner.ScheduleAble {
  public static ExtendEventInfo of(int evtId) {
    ExtendEventInfo ei = new ExtendEventInfo();
    ei.eventId    = evtId;
    ei.eventName  = "";
    ei.active     = true;
    ei.startTime  = -1;
    ei.endTime    = -1;
    ei.flushDelay = 0;
    //funny error
    ei.idolList   = new HashMap<>();
    return ei;
  }

  @Override
  public void updateTime(String startDate, String endDate, int flushDelay) {
    try {
      int newStart    = (int)(Utilities.getMillisFromDateString(startDate, Constant.DATE_PATTERN)/1000);
      int newEnd      = (int)(Utilities.getMillisFromDateString(endDate, Constant.DATE_PATTERN)/1000);

      int second    = (int)(System.currentTimeMillis()/1000);
      if (newStart - second <= 61)
        throw new IllegalArgumentException(String.format("start time < current time, evtId:%d", this.eventId));

      if (newStart <= endTime + this.flushDelay)
        throw new IllegalArgumentException(String.format("start time < last flush time, evtId:%d", this.eventId));

      if (newEnd - newStart <= 61)
        throw new IllegalArgumentException(String.format("end time < start time, evtId:%d", this.eventId));

      startTime       = newStart;
      endTime         = newEnd;
      this.flushDelay = flushDelay > 0 ? flushDelay : EventInfo.FLUSH_DELAY;
    }
    catch (Exception e) {
      LOG.globalException("node", "updateTime", e);
    }
  }

  public void addIdol(IdolClaimInfo icp) {
    if (icp != null)
      idolList.put(icp.idolId, icp);
  }
}