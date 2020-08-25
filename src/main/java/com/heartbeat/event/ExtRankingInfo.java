package com.heartbeat.event;

import com.common.Constant;
import com.common.GlobalVariable;
import com.common.Utilities;
import com.heartbeat.model.data.UserRanking;
import com.statics.EventInfo;
import com.statics.RankingInfo;

import java.util.concurrent.TimeUnit;

public class ExtRankingInfo extends RankingInfo {
  public static ExtRankingInfo of(int id) {
    ExtRankingInfo ri   = new ExtRankingInfo();
    ri.eventId          = id;
    ri.eventName        = "";
    ri.startTime        = -1;
    ri.endTime          = -1;
    ri.flushDelay       = EventInfo.FLUSH_DELAY;
    ri.active           = true;
    return ri;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public ExtRankingInfo updateEventTime(String strStart, String strEnd) {

    try {
      int newStart   = (int)(Utilities.getMillisFromDateString(strStart, Constant.DATE_PATTERN)/1000);
      int newEnd     = (int)(Utilities.getMillisFromDateString(strEnd, Constant.DATE_PATTERN)/1000);

      int second    = (int)(System.currentTimeMillis()/1000);
      if (newStart - second <= 60) //start time must after current as lease 60 seconds;
        throw new IllegalArgumentException("new start time is after current time");

      if (newStart <= endTime + EventInfo.FLUSH_DELAY)
        throw new IllegalArgumentException("new start time is before flush time");

      if (newEnd - newStart <= 60) //end time must after start time as lease 60 seconds;
        throw new IllegalArgumentException("end time < start time");

      startTime     = newStart;
      endTime       = newEnd;
      int flushTime = endTime + EventInfo.FLUSH_DELAY;
      flushDelay    = EventInfo.FLUSH_DELAY;

      GlobalVariable.schThreadPool.schedule(() -> UserRanking.openRanking(this.eventId),
              startTime - second, TimeUnit.SECONDS);
      GlobalVariable.schThreadPool.schedule(() -> UserRanking.closeRanking(this.eventId),
              endTime - second, TimeUnit.SECONDS);
      GlobalVariable.schThreadPool.schedule(() -> UserRanking.flushRanking(this.eventId),
              flushTime - second, TimeUnit.SECONDS);
    }
    catch (Exception e) {
      startTime     = -1;
      endTime       = -1;
    }
    return this;
  }
}