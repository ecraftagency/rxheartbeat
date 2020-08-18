package com.heartbeat.ranking.impl;

import com.heartbeat.ranking.EventLoop;
import com.statics.Common;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.HashMap;
import java.util.Map;

public class GetAllRankCommand<K, V extends Comparable<V> & Common.hasKey<K>> implements EventLoop.Command {
  K key;
  Handler<AsyncResult<Map<Integer, Integer>>> handler;
  Map<Integer, LeaderBoard<K, V>> rankings;

  public GetAllRankCommand(K key, Map<Integer, LeaderBoard<K, V>> rankings, Handler<AsyncResult<Map<Integer,Integer>>> handler) {
    this.key      = key;
    this.handler  = handler;
    this.rankings = rankings;
  }
  @Override
  public void execute() {
    Map<Integer, Integer> result = new HashMap<>();
    int rank;
    for (Map.Entry<Integer, LeaderBoard<K,V>> entry : rankings.entrySet()) {
      rank = entry.getValue().getRank(key);
      result.put(entry.getKey(), rank);
    }

    handler.handle(Future.succeededFuture(result));
  }
}