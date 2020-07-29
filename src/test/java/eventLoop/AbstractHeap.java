package eventLoop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public abstract class AbstractHeap<K,V> {
  protected Map<K,V> indexer;
  protected PriorityQueue<V> sorter;
  protected int capacity;

  public AbstractHeap(int capacity) {
    this.capacity = capacity;
    indexer = new HashMap<>();
  }

  public abstract void record(V val);
  public abstract List<V> get();
  public void flush() {
    indexer.clear();
    sorter.clear();
  }
}