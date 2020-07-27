import java.util.Arrays;
import java.util.List;

public class BitStream {
  public static void main(String[] args) {
    List<Long> achievementClaim = Arrays.asList(0L,0L,0L,0L,0L);
    recordClaim(achievementClaim, 261);
    recordClaim(achievementClaim, 262);

    System.out.println(checkClaim(achievementClaim, 261));
    System.out.println(checkClaim(achievementClaim, 262));

  }

  public static void recordClaim(List<Long> ar, int milestone) {
    try {
      int idx = milestone/64;
      int shift = milestone%64;
      Long segment = ar.get(idx);
      segment |= (1<<shift);
      ar.set(idx, segment);
    }
    catch (Exception e) {
      //
    }
  }

  public static boolean checkClaim(List<Long> ar, int milestone) {
    try {
      int idx = milestone/64;
      int shift = milestone%64;
      Long segment = ar.get(idx);
      long mask = 1<<shift;
      return ((segment&mask) > 0);
    }
    catch (Exception e) {
      return false;
    }
  }

  public static void printBit(List<Long> ar) {
    for (long l : ar) {
      System.out.print(longToString(l,64));
      System.out.print(" ");

    }
  }

  public static String longToString(long number, int groupSize) {
    StringBuilder result = new StringBuilder();

    for(int i = 63; i >= 0 ; i--) {
      int mask = 1 << i;
      result.append((number & mask) != 0 ? "1" : "0");

      if (i % groupSize == 0)
        result.append(" ");
    }
    result.replace(result.length() - 1, result.length(), "");

    return result.toString();
  }
}
