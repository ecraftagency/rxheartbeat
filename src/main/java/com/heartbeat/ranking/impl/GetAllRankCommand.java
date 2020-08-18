package com.heartbeat.ranking.impl;

import com.heartbeat.ranking.EventLoop;
import com.statics.Common;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetAllRankCommand<K, V extends Comparable<V> & Common.hasKey<K>> implements EventLoop.Command {
  K key;
  Handler<AsyncResult<List<Integer>>> handler;
  Map<Integer, LeaderBoard<K, V>> rankings;

  public GetAllRankCommand(K key, Map<Integer, LeaderBoard<K, V>> rankings, Handler<AsyncResult<List<Integer>>> handler) {
    this.key      = key;
    this.handler  = handler;
    this.rankings = rankings;
  }
  @Override
  public void execute() {
    List<Integer> result = new ArrayList<>();
    int rank;
    for (LeaderBoard<K, V> ldb : rankings.values()) {
      rank = ldb.getRank(key);
      result.add(rank);
    }

    handler.handle(Future.succeededFuture(result));
  }
}