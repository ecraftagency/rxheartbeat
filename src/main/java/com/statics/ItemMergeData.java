package com.statics;

import java.util.List;
import java.util.Map;

public class ItemMergeData {
  public static class ItemMergeDto implements Common.hasKey<Integer> {
    public int id;
    public List<Integer>        product;
    public List<List<Integer>>  materials;
    public int                  dailyLimit;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, ItemMergeDto> itemMergeDtoMap;

  public static void loadJson(String jsonText) {
    itemMergeDtoMap = Common.loadMap(jsonText, ItemMergeDto.class);
  }
}
