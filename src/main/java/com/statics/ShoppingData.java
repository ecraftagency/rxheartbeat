package com.statics;

import java.util.ArrayList;
import java.util.Arrays;
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

  @FunctionalInterface
  public interface MakeRand {
    void randProps(Shopping shopping);
  }

  public static MakeRand randomer = shopping -> {

  };

  public static Map<Integer, Shopping>  shoppingMap;
  public static List<List<Integer>>     shoppingReward;
  public static List<Integer>           nItemPerShopping;

  static {
    List<Integer> pack1 = Arrays.asList(26,27,28,29,60,35);
    List<Integer> pack2 = Arrays.asList(2,1,103,105,5,60,35);
    List<Integer> pack3 = Arrays.asList(64,70,71,72,5,40,44,48,61);
    List<Integer> pack4 = Arrays.asList(108,64,61,5,65,57,41,45,49,106,91);
    List<Integer> pack5 = Arrays.asList(108,57,65,66,42,46,50,5,91,107,104);
    shoppingReward      = Arrays.asList(pack1, pack2, pack3, pack4, pack5);
    nItemPerShopping    = Arrays.asList(1,2,3,4,5);
  }

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
    randomer.randProps(clone);
    return clone;
  }
}
