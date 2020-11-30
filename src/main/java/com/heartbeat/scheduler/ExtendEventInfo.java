package com.heartbeat.scheduler;

import com.common.Constant;
import com.common.LOG;
import com.common.Utilities;
import com.heartbeat.HBServer;
import com.statics.EventInfo;

import java.util.HashMap;

public class ExtendEventInfo extends EventInfo implements TaskRunner.ScheduleAble {
  public static ExtendEventInfo of(int evtId, int rewardPack) {
    ExtendEventInfo ei = new ExtendEventInfo();
    ei.eventId    = evtId;
    ei.eventName  = "";
    ei.active     = true;
    ei.startTime  = -1;
    ei.endTime    = -1;
    ei.flushDelay = 0;
    ei.rewardPack = rewardPack;
    //funny error
    ei.idolList   = new HashMap<>();
    return ei;
  }

  @Override
  public boolean updateTime(String startDate, String endDate, int flushDelay, int rewardPack) {
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
      this.rewardPack = rewardPack;
      return true;
    }
    catch (Exception e) {
      LOG.globalException(String.format("node_%d", HBServer.nodeId),
              String.format("updateTime, evtId:%d", eventId), e);
      return false;
    }
  }

  public void addIdol(IdolClaimInfo icp) {
    if (icp != null)
      idolList.put(icp.idolId, icp);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ExtendEventInfo) {
      ExtendEventInfo eii = (ExtendEventInfo)obj;
      return eii.eventId == this.eventId && eii.startTime == this.startTime
              && eii.endTime == this.endTime && eii.flushDelay == this.flushDelay;
    }
    return false;
  }
}