package com.heartbeat.ranking.impl;

import com.heartbeat.ranking.EventLoop;
import com.statics.Common;

public class RecordCommand<K, V extends Comparable<V> & Common.hasKey<K>> implements EventLoop.Command {
  LeaderBoard<K, V>  ldb;
  V                  so;

  public RecordCommand(LeaderBoard<K, V> ldb, V so) {
    this.ldb = ldb;
    this.so  = so;
  }

  @Override
  public void execute() {
    ldb.record(so.mapKey(), so);
  }
}