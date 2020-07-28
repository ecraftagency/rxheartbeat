import java.util.*;

public class TreeOfFact {
  public static int PROD          = 1;
  public static int PROD_CRT      = 3;
  public static int PROD_VIEW     = 2;
  public static int PROD_FAN      = 1;

  public static int IDOL          = 2;
  public static int IDOL_LV       = 1;

  public static int USER          = 3;
  public static int USER_LV       = 1;


  public static int USER_TALENT   = 2;

  public static int MEDIA         = 4;

  public static int FIGHT         = 5;


  public static class Node {
    int                     activityId;
    int                     value;
    TreeMap<Integer, Node>  child;
    Node                    parent;

    public void incr(List<Integer> format) {
      if (format.size() == 0)
        return;

      int activityID = format.get(0);
      if (!child.containsKey(activityID))
        child.put(activityID, Node.of(activityID, 0, this));
      Node node = child.get(activityID);
      if (format.size() == 1)
        node.value++;
      else
        node.incr(format.subList(1, format.size()));
    }

    public void set(List<Integer> format) {
      if (format.size() == 0)
        return;

      int activityID = format.get(0);
      if (!child.containsKey(activityID))
        child.put(activityID, Node.of(activityID, 0, this));
      Node node = child.get(activityID);
      if (format.size() == 2)
        node.value = format.get(1);
      else
        node.set(format.subList(1, format.size()));
    }

    public boolean query(List<Integer> format) {
      if (format.size() == 0)
        return false;
      if (format.size() == 2) {
        Node node = child.get(format.get(0));
        if (node == null)
          return false;
        return node.value >= format.get(1);
      }
      Node node = child.get(format.get(0));
      if (node == null)
        return false;
      return node.query(format.subList(1,format.size()));
    }

    public Node(int activityId, int value, Node parent) {
      this.activityId = activityId;
      this.value      = value;
      this.child      = new TreeMap<>();
      this.parent     = parent;
    }

    public Node() {

    }

    public static Node of(int activityId, int value, Node parent) {
      Node node       = new Node();
      node.activityId = activityId;
      node.value      = value;
      node.parent     = parent;
      node.child      = new TreeMap<>();
      return node;
    }
  }

  static void LevelOrderTraversal(Node root) {
    if (root == null)
      return;
    Queue<Node > q = new LinkedList<>(); // Create a queue
    q.add(root); // Enqueue root
    while (!q.isEmpty()) {
      int n = q.size();
      while (n > 0) {
        Node p = q.peek();
        q.remove();
        if (p != null) {
          System.out.print(p.activityId + " " + p.value + " ");
          q.addAll(p.child.values());
        }
        n--;
      }
      System.out.println();
    }
  }

  public static void main(String[] args) {
    Node root = Node.of(0, 0, null);
    root.child.put(IDOL, new Node(IDOL, 0, root) {
      @Override
      public boolean query(List<Integer> format) {
        if (format.size() != 3)
          return false;
        int queryType   = format.get(0);

        if (queryType == 0) {
          int value       = format.get(1);
          int count       = format.get(2);

          int cnt         = 0;
          for (Map.Entry<Integer, Node> entry : child.entrySet()) {
            if (entry.getValue().value >= value)
              cnt++;
          }
          return cnt >= count;
        }
        else if (queryType == 1) {
          int idolID      = format.get(1);
          int level       = format.get(2);
          for (Map.Entry<Integer, Node> entry : child.entrySet())
            if (entry.getKey() == idolID && entry.getValue().value >= level)
              return true;
          return false;
        }
        return false;
      }
    });

    for (int i = 0; i < 200; i++)
      root.incr(Arrays.asList(PROD,PROD_CRT));
    for (int i = 0; i < 20; i++)
      root.incr(Arrays.asList(IDOL, 1));
    for (int i = 0; i < 20; i++)
      root.incr(Arrays.asList(IDOL, 2));
    for (int i = 0; i < 20; i++)
      root.incr(Arrays.asList(IDOL, 3));

    for (int i = 0; i < 16; i++)
      root.incr(Arrays.asList(FIGHT));

    root.set(Arrays.asList(USER, USER_LV, 10));

    root.set(Arrays.asList(MEDIA, 3));


//    root.set(Arrays.asList(IDOL, 1, 20));
//    root.set(Arrays.asList(IDOL, 2, 20));
//    root.set(Arrays.asList(IDOL, 3, 20));


    List<Integer> query = Arrays.asList(PROD, PROD_CRT, 199);
    System.out.println(root.query(query));

    System.out.println(root.query(Arrays.asList(USER, USER_LV, 11)));

    //Ít nhất 2 level đạt level 20
    System.out.println(root.query(Arrays.asList(IDOL, 0, 20, 3)));

    //Idol Chi Pu lên cấp 10
    System.out.println(root.query(Arrays.asList(IDOL, 1, 1, 20)));

    //Đi ải dc 16 lần
    System.out.println(root.query(Arrays.asList(FIGHT, 16)));


    //xử lí truyền thông 3 lần
    System.out.println(root.query(Arrays.asList(MEDIA, 3)));
    LevelOrderTraversal(root);
  }
}