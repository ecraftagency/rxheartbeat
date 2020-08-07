package com.heartbeat.ranking.impl;

import com.heartbeat.ranking.EventLoop;
import com.statics.Common;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class GetRankCommand<K, V extends Comparable<V> & Common.hasKey<K>> implements EventLoop.Command {
  K key;
  LeaderBoard<K, V> ldb;
  Handler<AsyncResult<Integer>> handler;
  public GetRankCommand(LeaderBoard<K, V> ldb, K key, Handler<AsyncResult<Integer>> handler) {
    this.key      = key;
    this.ldb      = ldb;
    this.handler  = handler;
  }
  @Override
  public void execute() {
    int result = ldb.getRank(key);
    handler.handle(Future.succeededFuture(result));
  }
}