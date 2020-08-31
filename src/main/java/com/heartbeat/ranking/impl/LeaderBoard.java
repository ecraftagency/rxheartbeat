package com.heartbeat.ranking.impl;
import com.common.LOG;
import com.heartbeat.ranking.Heap;
import com.statics.Common;

import java.util.*;

//pls don't ask me about this after 3 months :D
//make sure only one thread at a time can operation on this Heap [!-NO SYNCHRONIZATION @ALL-!]
public class LeaderBoard<K, V extends Comparable<V> & Common.hasKey<K>> implements Heap<K, V> {
  protected Map<K,V>  indexer;
  protected Queue<V>  sorter;
  protected List<V>   achiever;
  private   int       capacity;
  private   boolean   recordLock;

  public LeaderBoard(int capacity) {
    indexer         = new HashMap<>();
    sorter          = new PriorityQueue<>(Comparable::compareTo);
    achiever        = new ArrayList<>();
    this.capacity   = capacity;
    this.recordLock = true;
  }

  public LeaderBoard(int capacity, List<V> initValue, boolean recordLock) {
    indexer         = new HashMap<>();
    sorter          = new PriorityQueue<>(Comparable::compareTo);
    achiever        = new ArrayList<>();
    this.capacity   = capacity;
    this.recordLock = recordLock;
    for (V v : initValue)
      record(v.mapKey(), v);
  }

  @Override
  public void record(K key, V val) {
    if (recordLock)
      return;
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

  public void open() {
    recordLock = false;
  }

  @Override
  public void flush() {
    sorter.clear();
    indexer.clear();
    achiever.clear();
    LOG.console("flush ranking");
  }

  public void close() {
    achiever  = new ArrayList<>(sorter);
    recordLock = true;
    achiever.sort(Comparator.reverseOrder());
    LOG.console("close ranking");
  }

  public int getRank(K key) {
    int idx = 0;
    for (V v : achiever) {
      if (key.equals(v.mapKey()))
        return idx + 1;
      idx++;
    }

    return -1;
  }
}