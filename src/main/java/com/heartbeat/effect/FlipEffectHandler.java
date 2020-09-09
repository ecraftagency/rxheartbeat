package com.heartbeat.effect;

import com.common.Constant;
import com.common.Msg;
import com.heartbeat.model.Session;
import com.transport.EffectResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlipEffectHandler implements EffectHandler{
  public static final int IDOL_ITEM_CATEGORY      = 1;
  private Map<Integer, List<Integer>> itemTable;

  public FlipEffectHandler() {
    itemTable = new HashMap<>();
    itemTable.put(IDOL_ITEM_CATEGORY, Arrays.asList(67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90));
  }

  @Override
  public String handleEffect(ExtArgs extArgs, Session session, List<Integer> effectFormat) {
    int category = effectFormat.get(PARAM1);
    int amount   = effectFormat.get(PARAM2);
    int itemId   = extArgs.intParam;

    List<Integer> itemList = itemTable.get(category);
    if (itemList == null || !itemList.contains(itemId))
      return Msg.map.getOrDefault(Msg.MALFORM_ARGS, "malform_args");

    session.userInventory.addItem(itemId, amount);
    session.effectResults.add(EffectResult.of(Constant.EFFECT_RESULT.ITEM_EFFECT_RESULT,itemId, amount));
    return "ok";
  }
}
