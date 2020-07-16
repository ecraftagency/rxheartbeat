package com.tulinh.dto;

public class Inventory {
  public int      type;
  public String   name;
  public int      amount;
  public boolean  is_gift;
  public int      upgrade;
  public int      condi_merge;

  public static Inventory of(int type, String name, int amount, boolean is_gift, int upgrade, int condi_merge) {
    Inventory inv = new Inventory();
    inv.type = type;
    inv.name = name;
    inv.amount = amount;
    inv.is_gift = is_gift;
    inv.upgrade = upgrade;
    inv.condi_merge = condi_merge;
    return inv;
  }
}
