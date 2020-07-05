package com.statics;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookLimitData {
  public static class BookLimitDTO {
    public int id;
    public int specialityID;
    public int level_200;
    public int level_300;
    public int level_350;
    public int level_400;
    public int level_500;
  }

  public static class BookLimit {
    public int idolId;
    public Map<Integer, List<Integer>> limits;
    public static BookLimit of(int idolId, Map<Integer, List<Integer>> limits) {
      BookLimit res = new BookLimit();
      res.idolId = idolId;
      res.limits = limits;
      return res;
    }
  }

  public static Map<Integer, BookLimit> bookLimitMap;
  public static Map<Integer, Integer> limitMap;

  static {
    limitMap = new HashMap<>();
    limitMap.put(0, 200);
    limitMap.put(1, 300);
    limitMap.put(2, 350);
    limitMap.put(3, 400);
    limitMap.put(4, 500);

    bookLimitMap = new HashMap<>();
  }

  public static int getCurrentLimit(int idolId, int specialityId, int currentLevel) {
    BookLimit bl = bookLimitMap.get(idolId);
    if (bl == null)
      return -1;
    List<Integer> limits = bl.limits.get(specialityId);
    if (limits == null)
      return -1;
    int idx = -1;
    for (Map.Entry<Integer, Integer> entry : limitMap.entrySet())
      if (currentLevel <= entry.getValue()) {
        idx = entry.getKey();
        break;
      }
    if (idx < 0 || idx > 4)
      return -1;
    if (limits.size() != 5)
      return -1;

    return limits.get(idx);
  }

  public static void loadJson(String jsonText) {
    List<BookLimitDTO> bookLimitDTOList = Common.loadList(jsonText, BookLimitDTO.class);
    for (BookLimitDTO bld : bookLimitDTOList) {
      if (bookLimitMap.get(bld.id) == null) {
        BookLimit blm = BookLimit.of(bld.id, new HashMap<>());
        List<Integer> limits = Arrays.asList(bld.level_200,bld.level_300,bld.level_350,bld.level_400,bld.level_500);
        blm.limits.put(bld.specialityID, limits);
        bookLimitMap.put(bld.id, blm);
      }
      else {
        BookLimit blm = bookLimitMap.get(bld.id);
        List<Integer> limits = Arrays.asList(bld.level_200,bld.level_300,bld.level_350,bld.level_400,bld.level_500);
        blm.limits.put(bld.specialityID, limits);
      }
    }

    //valid date
    for (BookLimit bl : bookLimitMap.values()) {
      if (bl.limits.values().size() != 3) {
        System.out.println("Book limit data inconsistency");
        bookLimitMap.clear();
        break;
      }
    }
  }
}
