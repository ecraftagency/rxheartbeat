package com.heartbeat.ranking;

import com.statics.Common;
import java.util.List;

public interface Heap<K,V extends Comparable<?> & Common.hasKey<K>> {
  void    record(K key, V val);
  List<V> get();
  void    flush();
}