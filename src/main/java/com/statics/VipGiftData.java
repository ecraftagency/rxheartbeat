package com.statics;

import java.util.List;
import java.util.Map;

public class VipGiftData {
  public static class VipGiftDto implements Common.hasKey<Integer> {
    public int id;
    public int vipLevel;
    public List<List<Integer>> reward;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, VipGiftDto> vipGiftDtoMap;

  public static void loadJson(String jsonText) {
    vipGiftDtoMap = Common.loadMap(jsonText, VipGiftDto.class);
  }
}