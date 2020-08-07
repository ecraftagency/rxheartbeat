package com.heartbeat.ranking;

import com.heartbeat.common.GlobalVariable;
import com.heartbeat.common.Utilities;
import com.heartbeat.model.data.UserRanking;
import com.statics.RankingInfo;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.heartbeat.common.Constant.RANKING.*;

public class ExtRankingInfo extends RankingInfo {
  public static ExtRankingInfo of() {
    ExtRankingInfo ri   = new ExtRankingInfo();
    ri.strEnd           = "";
    ri.strStart         = "";
    ri.startTime        = -1;
    ri.endTime          = -1;
    ri.activeRankings = new HashMap<>();
    ri.activeRankings.put(13, false);
    ri.activeRankings.put(14, false);
    ri.activeRankings.put(15, false);
    ri.activeRankings.put(16, false);
    ri.activeRankings.put(17, false);
    ri.flushDelay       = FLUSH_DELAY;
    return ri;
  }

  public void activeRanking(int rankingType, boolean active) {
    activeRankings.computeIfPresent(rankingType, (k,v) -> v = active);
  }

  public void setRankingTime(String strStart, String strEnd) {
    this.strStart = strStart;
    this.strEnd   = strEnd;

    try {
      int newStart   = (int)(Utilities.getMillisFromDateString(strStart, DATE_PATTERN)/1000);
      int newEnd     = (int)(Utilities.getMillisFromDateString(strEnd, DATE_PATTERN)/1000);

      int second    = (int)(System.currentTimeMillis()/1000);
      if (newStart - second <= 60) //start time must after current as lease 60 seconds;
        throw new IllegalArgumentException("new start time is after current time");

      if (newStart <= endTime + FLUSH_DELAY)
        throw new IllegalArgumentException("new start time is before flush time");

      if (newEnd - newStart <= 60) //end time must after start time as lease 60 seconds;
        throw new IllegalArgumentException("end time < start time");

      startTime     = newStart;
      endTime       = newEnd;
      int flushTime = endTime + FLUSH_DELAY;
      flushDelay    = FLUSH_DELAY;

      GlobalVariable.schThreadPool.schedule(UserRanking::openAllRanking,
              startTime - second, TimeUnit.SECONDS);
      GlobalVariable.schThreadPool.schedule(UserRanking::closeAllRanking,
              endTime - second, TimeUnit.SECONDS);
      GlobalVariable.schThreadPool.schedule(UserRanking::flushAllRanking,
              flushTime - second, TimeUnit.SECONDS);
    }
    catch (Exception e) {
      startTime     = -1;
      endTime       = -1;
      this.strStart = "";
      this.strEnd   = "";
    }
  }
}
