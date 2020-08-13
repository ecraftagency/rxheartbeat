package com.statics;
import java.util.List;
import java.util.Map;

public class VipData {
  public static class VipDto implements Common.hasKey<Integer> {
    public int            level;
    public int            exp;
    public int            travelLimit;
    public int            travelAddLimit;
    public int            createLimit;
    public int            idolLevelUpLimit;
    public List<Integer>  rewardFormat;

    @Override
    public Integer mapKey() {
      return level;
    }
  }

  public static Map<Integer, VipDto> vipMap;

  public static void loadJson(String jsonText) {
    vipMap = Common.loadMap(jsonText, VipDto.class);
  }

  public static VipDto getVipData(int exp) {
    if (exp <= 0)
      return VipData.vipMap.get(0);
    int idx = 0;
    for (VipDto vip : vipMap.values()) {
      if (exp < vip.exp) {
        return vipMap.get(idx-1);
      }
      idx++;
    }
    return ofNullObject();
  }

  public static VipDto ofNullObject() {
    VipDto vip            = new VipDto();
    vip.level             = 15;
    vip.exp               = 2000000;
    vip.travelLimit       = 20;
    vip.travelAddLimit    = 400;
    vip.createLimit       = 6;
    vip.idolLevelUpLimit  = 6;
    return vip;
  }
}