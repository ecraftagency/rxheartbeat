import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Test {
  public static void main(String[] args) {
    Map<Integer, Integer> intMap = new HashMap<>();
    intMap.putIfAbsent(0, 3);
    intMap.putIfAbsent(1, 4);
    intMap.putIfAbsent(2, 5);
    intMap.putIfAbsent(1, 6); //****

    //entry iteration
    for (Map.Entry<Integer, Integer> entry : intMap.entrySet()) {
      System.out.println(entry.getKey()); // 0,1,2
      System.out.println(entry.getValue()); //2,6,5
    }

    //key iteration
    for (int key : intMap.keySet())
      System.out.println(key);

    //value iteration
    for (int val : intMap.values())
      System.out.println(val);

    //aggregate operation (cac phep toan tong hop)

    //1 - x2 gia tri cua object có key = 0, voi dieu kien co ton tai key do
    intMap.computeIfPresent(0, (k,v) -> v *= 2);

    //filter cac gia tri chan
    List<Integer> evenValue = intMap.values().stream().filter(v -> v%2 == 0).collect(Collectors.toList()); //[6]

    //sum key
    int sumKey = intMap.keySet().stream().reduce(Integer::sum).get(); //sumKey = 3
    System.out.println(sumKey);
  }

  static int pad(int inp) {
    while (inp/1000000000 == 0) {
      inp*=10;
    }
    return inp;
  }
}