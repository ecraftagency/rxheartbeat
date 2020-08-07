package com.heartbeat.ranking.impl;

import com.heartbeat.ranking.EventLoop;
import com.statics.Common;

public class CloseCommand<K, V extends Comparable<V> & Common.hasKey<K>> implements EventLoop.Command {
  LeaderBoard<K, V>             ldb;
  public CloseCommand(LeaderBoard<K, V> ldb) {
    this.ldb = ldb;
  }
  @Override
  public void execute() {
    ldb.close();
  }
}