package com.heartbeat.event;

import com.common.Constant;
import com.common.GlobalVariable;
import com.common.LOG;
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
    ri.flushDelay       = 0;
    ri.active           = true;
    return ri;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public ExtRankingInfo updateEventTime(String strStart, String strEnd, int flushDelay) {

    try {
      int newStart    = (int)(Utilities.getMillisFromDateString(strStart, Constant.DATE_PATTERN)/1000);
      int newEnd      = (int)(Utilities.getMillisFromDateString(strEnd, Constant.DATE_PATTERN)/1000);

      int second    = (int)(System.currentTimeMillis()/1000);
      if (newStart - second <= 60)
        throw new IllegalArgumentException("[rank event] start time < current time");

      if (newStart <= endTime + this.flushDelay)
        throw new IllegalArgumentException("[rank event] start time < last flush time");

      if (newEnd - newStart <= 60)
        throw new IllegalArgumentException("[rank event] end time < start time");


      startTime       = newStart;
      endTime         = newEnd;
      this.flushDelay = flushDelay > 0 ? flushDelay : EventInfo.FLUSH_DELAY;
      int flushTime   = endTime + flushDelay;

      GlobalVariable.schThreadPool.schedule(() -> UserRanking.openRanking(this.eventId),
              startTime - second, TimeUnit.SECONDS);
      GlobalVariable.schThreadPool.schedule(() -> UserRanking.closeRanking(this.eventId),
              endTime - second, TimeUnit.SECONDS);
      GlobalVariable.schThreadPool.schedule(() -> UserRanking.flushRanking(this.eventId),
              flushTime - second, TimeUnit.SECONDS);
    }
    catch (Exception e) {
      LOG.globalException(e);
    }
    return this;
  }
}