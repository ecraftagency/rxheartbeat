public class Test {
  public static void main(String[] args) {
    System.out.println(pad(135));
    //00000012344
    //12340000000
  }

  static int pad(int inp) {
    while (inp/1000000000 == 0) {
      inp*=10;
    }
    return inp;
  }
}