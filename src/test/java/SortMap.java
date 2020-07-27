import java.util.TreeMap;

public class SortMap {
  public static class Val implements Comparable<Val>{
    public int id;
    public int score;
    public static Val of(int id, int score) {
      Val val = new Val();
      val.id = id;
      val.score = score;
      return val;
    }

    @Override
    public int compareTo(Val o) {
      return score - o.score;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Val)) return false;

      Val val = (Val) o;

      return id == val.id;
    }

    @Override
    public int hashCode() {
      return id;
    }
  }
  public static void main(String[] args) {
    TreeMap<Integer, Val> ss = new TreeMap<>();
    ss.put(3, Val.of(1,20));
    ss.put(2, Val.of(1,10));
    for (Val val : ss.values())
      System.out.println(val.score);
  }
}
