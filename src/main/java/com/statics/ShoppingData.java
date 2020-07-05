package com.statics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShoppingData {
  public static class Shopping implements Common.hasKey<Integer> {
    public int id;
    public long creativeNPC;
    public long moneyNPC;
    public List<Integer> reward;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, Shopping> shoppingMap;

  public static void loadJson(String jsonText) {
    shoppingMap = Common.loadMap(jsonText, Shopping.class);
  }

  public static Shopping of(int id) {
    Shopping clone = new Shopping();
    Shopping sp = shoppingMap.get(id);

    if (sp == null) {
      clone.id = -1;
      clone.creativeNPC = -1;
      clone.moneyNPC = -1;
      clone.reward = new ArrayList<>();
      return clone;
    }

    clone.id = sp.id;
    clone.moneyNPC = sp.moneyNPC;
    clone.creativeNPC = sp.creativeNPC;
    clone.reward = new ArrayList<>();
    clone.reward.addAll(sp.reward);
    return clone;
  }
}
