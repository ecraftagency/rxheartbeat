package com.heartbeat.model.data;

import com.heartbeat.common.Utilities;
import com.statics.PropData;
import com.transport.model.Inventory;

import java.util.concurrent.ConcurrentHashMap;

public class UserInventory extends Inventory {
  private UserInventory() {
    userItems = new ConcurrentHashMap<>();
  }

  public static UserInventory ofDefault() {
    UserInventory userInventory = new UserInventory();
    PropData.propMap.values().forEach(prop -> userInventory.addItem(prop.propID, 1000));
    return userInventory;
  }

  public String toJson() {
    return Utilities.gson.toJson(this);
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
}
