package com.statics;

import java.util.List;
import java.util.Map;

public class ShopData {
  public static class ShopDto implements Common.hasKey<Integer> {
    public int                  id;
    public int                  type;
    public String               desc;
    public int                  vipCond;
    public int                  timeCost;
    public int                  dailyLimit;
    public List<List<Integer>>  format;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, ShopDto> shopDtoMap;

  public static void loadJson(String jsonText) {
    shopDtoMap = Common.loadMap(jsonText, ShopDto.class);
  }
}
