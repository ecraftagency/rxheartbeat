package eventLoop;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class LDB extends AbstractHeap<Integer, ScoreObject> {
  public LDB(int capacity) {
    super(capacity);
    sorter = new PriorityQueue<>((a, b) -> (int) (a.score - b.score));
  }

  @Override
  public void record(ScoreObject val) {
    if (val == null)
      return;

    ScoreObject peek = sorter.peek();
    if (peek!=null && sorter.size() == capacity && val.score < peek.score)
      return;

    if (sorter.size() > capacity)
      sorter.poll();
    ScoreObject oldRecord = indexer.get(val.id);
    if (oldRecord != null) {
      sorter.remove(oldRecord);
    }
    sorter.add(val);
    indexer.put(val.id, val);
    if (sorter.size() > capacity) {
      ScoreObject remove = sorter.poll();
      if (remove != null)
        indexer.remove(remove.id);
    }
  }

  @Override
  public List<ScoreObject> get() {
    return new ArrayList<>(sorter);
  }
}