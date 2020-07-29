package eventLoop.commands;

import java.util.Arrays;
import java.util.PriorityQueue;

public class queueTest {
  public static void main(String[] args) {
    PriorityQueue<Integer> q = new PriorityQueue<>(Arrays.asList(1, 2, 7, 3, 4, 5, 6));
    System.out.println(q.poll());
  }
}
