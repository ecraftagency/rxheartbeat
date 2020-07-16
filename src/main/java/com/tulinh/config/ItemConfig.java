package com.tulinh.config;

import com.tulinh.dto.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemConfig {

  public static List<Item> lsItem = new ArrayList<>();

  public static void initLsItem() {
    lsItem.add(Item.of(0, "The 10k", 1000, 8000, 0, true, -1, 0, -1, false));
    lsItem.add(Item.of(1, "The 50k", 500, 1500, 0, true, -1, 0, -1, false));
    lsItem.add(Item.of(2, "Manh ghep Long", 50000, 90000, 0, false, 6, 0, 10, true));
    lsItem.add(Item.of(3, "Manh ghep Lan", -1, 450000, 0, false, 7, 0, 10, true));
    lsItem.add(Item.of(4, "Manh ghep Quy", -1, 450000, 0, false, 8, 0, 10, true));
    lsItem.add(Item.of(5, "Manh ghep Phung", 100, 498, 0, false, 9, 0, 10, true));
    lsItem.add(Item.of(6, "The Long", 0, 0, 0, false, -1, 0, -1, true));
    lsItem.add(Item.of(7, "The Lan", 1, 1, 0, false, -1, 0, -1, true));
    lsItem.add(Item.of(8, "The Quy", 1, 1, 0, false, -1, 0, -1, true));
    lsItem.add(Item.of(9, "The Phung", 0, 0, 0, false, -1, 0, -1, true));
  }
}
