import java.util.*;

public class Learn {
  public static void main(String[] args) {
    List<Integer> list = Arrays.asList(1,2,3,4,5,6);

    Set<Integer> set = new HashSet<>(); //tap hop ->
    set.add(1);
    set.add(2);
    set.add(3);
    set.add(1);
    System.out.println(set);

    HashMap<Integer, Boolean> map  = new HashMap<>();
    map.put(1,false);
    map.put(2, true);
    map.put(3, false);

    for (Map.Entry<Integer, Boolean> entry : map.entrySet()) {
      System.out.println("key: " + entry.getKey() + " " + "value: " + entry.getValue());
    }

    for (Integer key : map.keySet()) {
      System.out.println("key: " + key);
    }

    for (Boolean value : map.values()) {
      System.out.println("value: " + value);
    }
  }
}
