package com.statics;

import java.util.List;
import java.util.Map;

public class DailyGiftData {
  public static class DailyGiftDto implements Common.hasKey<Integer> {
    public int id;
    public List<List<Integer>> reward;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, DailyGiftDto> dailyGiftDtoMap;

  public static void loadJson(String jsonText) {
    dailyGiftDtoMap = Common.loadMap(jsonText, DailyGiftDto.class);
  }
}