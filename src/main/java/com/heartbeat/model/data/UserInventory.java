package com.heartbeat.model.data;

import com.common.Msg;
import com.heartbeat.db.cb.AbstractCruder;
import com.heartbeat.db.dao.ItemStatsDAO;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.ItemMergeData;
import com.statics.PropData;
import com.transport.model.Inventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class UserInventory extends Inventory {
  public  static ConcurrentHashMap<Integer, Integer>  itemStats;
  public  static AbstractCruder<ItemStatsDAO>         cbItemStats;
  private static final String dbKey = "ItemStats";

  static {
    cbItemStats = new AbstractCruder<>(ItemStatsDAO.class);
    itemStats   = new ConcurrentHashMap<>();
    itemStats.putIfAbsent(123,0);
    itemStats.putIfAbsent(124,0);
    itemStats.putIfAbsent(125,0);
    itemStats.putIfAbsent(126,0);
    itemStats.putIfAbsent(127,0);
    itemStats.putIfAbsent(128,0);
    itemStats.putIfAbsent(129,0);
    itemStats.putIfAbsent(130,0);
    itemStats.putIfAbsent(131,0);
    itemStats.putIfAbsent(132,0);
    itemStats.putIfAbsent(133,0);
    itemStats.putIfAbsent(134,0);
  }

  public static void loadItemStatsFromDB() {
    ItemStatsDAO dao = cbItemStats.load(dbKey);
    if (dao == null)
      dao = ItemStatsDAO.ofDefault();
    itemStats.putAll(dao.itemCnt);
  }

  public static void syncItemStatToDB() {
    cbItemStats.sync(dbKey, ItemStatsDAO.of(itemStats));
  }

  private UserInventory() {
    userItems   =   new HashMap<>();
    expireItems =   new HashMap<>();
  }

  //shop data
  public Map<Integer, Integer> dailyMerge;

  public static UserInventory ofDefault() {
//    PropData.propMap.values().forEach(prop -> {
//      if (prop.status > 0) {
//        if (prop.isExpired >= 1) {
//          userInventory.addExpireItem(prop.propID, 10);
//        }
//        else {
//          userInventory.addStaticItem(prop.propID, 100);
//        }
//      }
//    });
    return new UserInventory();
  }

  public void newDay() {
    dailyMerge.clear();
  }

  public void reBalance() {
    if (dailyMerge == null)
      dailyMerge  = new HashMap<>();
    if (expireItems == null)
      expireItems = new HashMap<>();
  }

  public void addItem(int itemId, int amount) {
    if (amount <= 0)
      return;
    if (!PropData.propMap.containsKey(itemId))
      return;

    itemStats.computeIfPresent(itemId, (k,v) -> v + amount);
    if (isExpireItem(itemId)) {
      updateExpire();
      addExpireItem(itemId, amount);
    }
    else {
      addStaticItem(itemId, amount);
    }
  }

  public void removeItem(int itemId, int amount) {
    if (amount <= 0)
      return;
    if (!PropData.propMap.containsKey(itemId))
      return;
    if (!isExpireItem(itemId)) {
      removeStaticItem(itemId, amount);
    }
  }

  public boolean haveItem(int itemId, int amount) {
    if (isExpireItem(itemId)) {
      updateExpire();
      return haveExpireItem(itemId, amount);
    }
    else {
      return haveStaticItem(itemId, amount);
    }
  }

  public boolean useItem(int itemId, int amount) {
    if (isExpireItem(itemId)) {
      updateExpire();
      return userExpireItem(itemId, amount);
    }
    else {
      return userStaticItem(itemId, amount);
    }
  }

  public String mergeItem(Session session, int mergeId, int mergeCount) {
    ItemMergeData.ItemMergeDto dto = ItemMergeData.itemMergeDtoMap.get(mergeId);
    if (       dto == null
            || dto.materials == null
            || dto.materials.size() == 0
            || dto.product == null
            || dto.product.size() != 4)
      return Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND, "item_merge_data_not_found");

    if (dailyMerge.getOrDefault(mergeId, 0) > dto.dailyLimit)
      return Msg.map.getOrDefault(Msg.MERGE_DAILY_LIMIT, "merge_daily_limit");

    if (mergeCount <= 0)
      return Msg.map.getOrDefault(Msg.ZERO_MERGE_COUNT, "malform_args");

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
        return Msg.map.getOrDefault(Msg.INSUFFICIENT_MATERIAL, "insufficient_materials");
      }
    }
    catch (Exception e) {
      return Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "unknown_err");
    }
  }

  /*INTEGRITY CHECK****************************************************************************************************/
  private boolean isExpireItem(int itemId) {
    PropData.Prop prop = PropData.propMap.get(itemId);
    if (prop == null)
      return false;
    return prop.isExpired == 1;
  }

  private int getExpireTime(int itemId) {
    PropData.Prop prop = PropData.propMap.get(itemId);
    if (prop == null)
      return -1;
    return prop.expiredSeconds;
  }

  public UserInventory updateAndGet() {
    updateExpire();
    return this;
  }

  /*FOR STATIC ITEMS***************************************************************************************************/
  private void addStaticItem(int itemId, int amount) {
    if (userItems.containsKey(itemId)) {
      int oldAmount = userItems.get(itemId);
      userItems.put(itemId, oldAmount + amount);
    }
    else {
      userItems.put(itemId, amount);
    }
  }

  private void removeStaticItem(int itemId, int amount) {
    userItems.computeIfPresent(itemId, (k,v) -> v > amount ? v - amount : 0);
  }

  private boolean haveStaticItem(int itemId, int amount) {
    if (userItems.get(itemId) == null)
      return false;
    int actualAmount = userItems.get(itemId);
    return actualAmount >= amount;
  }

  private boolean userStaticItem(int itemId, int amount) {
    if (!haveStaticItem(itemId, amount))
      return false;
    int actualAmount = userItems.get(itemId);
    int remain = actualAmount - amount;
    userItems.put(itemId, remain);
    itemStats.computeIfPresent(itemId, (k,v) -> Math.max(v - amount, 0));
    return true;
  }

  /*FOR EXPIRE ITEMS***************************************************************************************************/
  private void updateExpire() {
    int curSec = (int)(System.currentTimeMillis()/1000);
    for (Map.Entry<Integer, List<Integer>> entry : expireItems.entrySet()) {
      int expireSec = getExpireTime(entry.getKey());
      if (expireSec < 0 || entry.getValue() == null || entry.getValue().size() == 0)
        continue;
      entry.getValue().removeIf(addedTime -> curSec - addedTime > expireSec);
    }
  }

  private void addExpireItem(int itemId, int amount) {
    int curSec = (int)(System.currentTimeMillis()/1000);
    if (!expireItems.containsKey(itemId)) {
      expireItems.put(itemId, new ArrayList<>());
    }

    List<Integer> expireVector = expireItems.get(itemId);
    for (int i = 0; i < amount; i++)
      expireVector.add(curSec);
  }

  private boolean haveExpireItem(int itemId, int amount) {
    if (expireItems.get(itemId) == null)
      return false;
    return expireItems.get(itemId).size() >= amount;
  }

  private boolean userExpireItem(int itemId, int amount) {
    if (!haveExpireItem(itemId, amount))
      return false;
    List<Integer> expireVector        = expireItems.get(itemId);
    List<Integer> newExp              = expireVector.subList(amount, expireVector.size());
    expireItems.put(itemId, newExp);
    itemStats.computeIfPresent(itemId, (k,v) -> Math.max(v - amount, 0));
    return true;
  }
}
