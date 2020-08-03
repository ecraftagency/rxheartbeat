package com.heartbeat.model.data;

import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.ItemMergeData;
import com.statics.PropData;
import com.transport.model.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserInventory extends Inventory {
  private UserInventory() {
    userItems = new ConcurrentHashMap<>();
  }

  //shop data
  public Map<Integer, Integer> dailyMerge;

  public static UserInventory ofDefault() {
    UserInventory userInventory = new UserInventory();
    PropData.propMap.values().forEach(prop -> userInventory.addItem(prop.propID, 1000));
    return userInventory;
  }

  public void newDay() {
    dailyMerge.clear();
  }

  public void reBalance() {
    if (dailyMerge == null)
      dailyMerge = new HashMap<>();
  }

  public void addItem(int itemId, int amount) {
    if (amount <= 0)
      return;
    if (!PropData.propMap.containsKey(itemId))
      return;
    if (userItems.containsKey(itemId)) {
      int oldAmount = userItems.get(itemId);
      userItems.put(itemId, oldAmount + amount);
    }
    else {
      userItems.put(itemId, amount);
    }
  }

  public   /*synchronized*/ boolean haveItem(int itemId, int amount) {
    if (userItems.get(itemId) == null)
      return false;
    int actualAmount = userItems.get(itemId);
    return actualAmount >= amount;
  }

  public /*synchronized*/ boolean useItem(int itemId, int amount) {
    if (!haveItem(itemId, amount))
      return false;
    int actualAmount = userItems.get(itemId);
    int remain = actualAmount - amount;
    userItems.put(itemId, remain);
    return true;
  }

  public /*synchronized*/ String mergeItem(Session session, int mergeId, int mergeCount) {
    ItemMergeData.ItemMergeDto dto = ItemMergeData.itemMergeDtoMap.get(mergeId);
    if (       dto == null
            || dto.materials == null
            || dto.materials.size() == 0
            || dto.product == null
            || dto.product.size() != 4)
      return "item_merge_data_not_found";

    if (dailyMerge.getOrDefault(mergeId, 0) > dto.dailyLimit)
      return "shop_limit";

    if (mergeCount <= 0)
      return "mission_impossible";

    try {
      boolean able = true;
      for (List<Integer> material : dto.materials) {
        int mId     = material.get(EffectHandler.PARAM1);
        int mAmount = mergeCount*material.get(EffectHandler.PARAM2);

        if (!haveItem(mId, mAmount)) {
          able = false;
          break;
        }
      }

      if (able) {
        for (List<Integer> material : dto.materials) {
          int mId           = material.get(EffectHandler.PARAM1);
          int consumeAmount = mergeCount*material.get(EffectHandler.PARAM2);
          useItem(mId, consumeAmount);
        }

        for (int i = 0; i < mergeCount; i++)
          EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, dto.product);

        int upt = dailyMerge.getOrDefault(mergeId, 0) + mergeCount;
        dailyMerge.put(mergeId, upt);
        return "ok";
      }
      else {
        return "insufficient_materials";
      }
    }
    catch (Exception e) {
      return "unknown_error";
    }
  }
}
