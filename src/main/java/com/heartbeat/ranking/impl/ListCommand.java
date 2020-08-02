package com.heartbeat.ranking.impl;

import com.heartbeat.ranking.EventLoop;
import com.statics.Common;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.List;

public class ListCommand<K, V extends Comparable<V> & Common.hasKey<K>> implements EventLoop.Command {
  Handler<AsyncResult<List<V>>> handler;
  LeaderBoard<K, V>             ldb;

  public ListCommand(LeaderBoard<K, V> ldb, Handler<AsyncResult<List<V>>> handler) {
    this.handler  = handler;
    this.ldb      = ldb;
  }

  @Override
  public void execute() {
    handler.handle(Future.succeededFuture(ldb.get()));
  }
}