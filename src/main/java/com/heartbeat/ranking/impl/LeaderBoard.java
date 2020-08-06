package com.heartbeat.ranking.impl;
import com.heartbeat.ranking.Heap;
import com.statics.Common;

import java.util.*;

//pls don't ask me about this after 3 months :D
public class LeaderBoard<K, V extends Comparable<V> & Common.hasKey<K>> implements Heap<K, V> {
  protected Map<K,V> indexer;
  protected Queue<V> sorter;
  private   int      capacity;

  public LeaderBoard(int capacity) {
    indexer       = new HashMap<>();
    sorter        = new PriorityQueue<>(Comparable::compareTo);
    this.capacity = capacity;
  }

  @Override
  public void record(K key, V val) {
    if (val == null)
      return;

    V peek = sorter.peek();
    if (peek!=null && sorter.size() == capacity && val.compareTo(peek) < 0)
      return;

    if (sorter.size() > capacity)
      sorter.poll();

    V oldRecord = indexer.get(key);
    if (oldRecord != null) {
      sorter.remove(oldRecord);
    }

    sorter.add(val);
    indexer.put(key, val);

    if (sorter.size() > capacity) {
      V remove = sorter.poll();
      if (remove != null)
        indexer.remove(remove.mapKey());
    }
  }

  @Override
  public List<V> get() {
    return new ArrayList<>(sorter);
  }

  @Override
  public void flush() {
    sorter.clear();
    indexer.clear();
    System.out.println("im clear!");
  }
}