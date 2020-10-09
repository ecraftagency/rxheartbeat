package com.statics;

import java.util.List;
import java.util.Map;

public class GoldenTimeData {
  public static class GoldenTimeDto implements Common.hasKey<Integer>{
    public int id;
    public List<List<Integer>> reward;
    public List<Integer> time;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, GoldenTimeDto> goldenTimeMap;

  public static void loadJson(String jsonText) {
    goldenTimeMap = Common.loadMap(jsonText, GoldenTimeDto.class);
  }
}
