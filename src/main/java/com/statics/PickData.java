package com.statics;
import java.util.List;
import java.util.Map;

public class PickData {
  public static class PickDto implements Common.hasKey<Integer>{
    public int id;
    public List<Integer> items;

    @Override
    public Integer mapKey() {
      return id;
    }
  }


  public static Map<Integer, PickDto> pickMap;
  public static void loadJson(String jsonText) {
    pickMap = Common.loadMap(jsonText, PickDto.class);
  }
}