package com.heartbeat.ranking.impl;

import com.common.LOG;
import com.heartbeat.ranking.EventLoop;
import com.heartbeat.scheduler.ExtendEventInfo;
import com.statics.Common;
import java.util.Map;

public class TimeCheckCommand<K, V extends Comparable<V> & Common.hasKey<K>> implements EventLoop.Command {
  Map<Integer, LeaderBoard<K, V>>   ldbMap;
  Map<Integer, ExtendEventInfo>     evtMap;
  int                               curSec;

  public TimeCheckCommand(Map<Integer, LeaderBoard<K, V>> ldbMap, Map<Integer, ExtendEventInfo> evtMap, long curMs) {
    this.ldbMap = ldbMap;
    this.evtMap = evtMap;
    this.curSec = (int)(curMs/1000);
  }
  @Override
  public void execute() {
    if (ldbMap == null || evtMap == null)
      return;

    for (Map.Entry<Integer, ExtendEventInfo> evtEntry : evtMap.entrySet()) {
      LeaderBoard<K, V> ldb = ldbMap.get(evtEntry.getKey());
      ExtendEventInfo eri    = evtEntry.getValue();
      if (ldb == null || eri == null || eri.startTime < 0 || eri.endTime < 0)
        continue;
      if (curSec > eri.endTime + eri.flushDelay) {
        ldb.flush();
        //LOG.console(String.format("LDB %d flush", eri.eventId));
      }
      else if (curSec > eri.endTime) {
        ldb.close();
        //LOG.console(String.format("LDB %d close", eri.eventId));
      }
      else if (curSec > eri.startTime) {
        ldb.open();
        //LOG.console(String.format("LDB %d open", eri.eventId));
      }
    }
  }
}