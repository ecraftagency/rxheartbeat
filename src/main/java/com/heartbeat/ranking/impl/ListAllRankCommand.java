package com.heartbeat.ranking.impl;

import com.heartbeat.ranking.EventLoop;
import com.statics.Common;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAllRankCommand<K, V extends Comparable<V> & Common.hasKey<K>> implements EventLoop.Command {
  K key;
  Handler<AsyncResult<Map<Integer, List<V>>>> handler;
  Map<Integer, LeaderBoard<K, V>> rankings;

  public ListAllRankCommand(K key, Map<Integer, LeaderBoard<K, V>> rankings, Handler<AsyncResult<Map<Integer, List<V>>>> handler) {
    this.key      = key;
    this.handler  = handler;
    this.rankings = rankings;
  }
  @Override
  public void execute() {
    Map<Integer, List<V>> result = new HashMap<>();
    for (Map.Entry<Integer, LeaderBoard<K,V>> entry : rankings.entrySet()) {
      List<V> currentList = entry.getValue().get();
      result.put(entry.getKey(), currentList);
    }

    handler.handle(Future.succeededFuture(result));
  }
}