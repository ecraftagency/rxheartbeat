package com.heartbeat.effect;

import com.heartbeat.model.Session;
import com.statics.PropData;
import com.transport.EffectResult;

import java.util.List;

public class InventoryEffectHandler implements EffectHandler{
  private static InventoryEffectHandler instance = new InventoryEffectHandler();
  @Override
  public String handleEffect(ExtArgs extArgs, Session session, final List<Integer> effectFormat) {
    int propId = effectFormat.get(PARAM1);
    int amount = effectFormat.get(PARAM2);
    if (PropData.propMap.get(propId) == null)
      return "item_not_exist";
    if (amount <= 0)
      return "zero or negative amount";
    session.userInventory.addItem(propId, amount);
    session.effectResults.add(EffectResult.of(1000,propId, amount));
    return EffectHandler.SUCCESS;
  }

  private InventoryEffectHandler() {

  }

  public static InventoryEffectHandler inst() {
    return instance;
  }
}
