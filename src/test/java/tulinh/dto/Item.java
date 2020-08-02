package tulinh.dto;

public class Item {
  public int        type;
  public String     name;
  public int        maximum;
  public int        percent;
  public int        amount;
  public boolean    save_in_gift;
  public int        upgrade;
  public int        remain;
  public int        condi_merge;
  public boolean    is_gift;

  public static Item of (int type, String name, int maximum, int percent, int amount, boolean save_in_gift, int upgrade, int remain, int condi_merge, boolean is_gift) {
    Item res      = new Item();
    res.type      = type;
    res.name      = name;
    res.maximum   = maximum;
    res.percent   = percent;
    res.amount    = amount;
    res.save_in_gift = save_in_gift;
    res.upgrade   = upgrade;
    res.remain    = remain;
    res.condi_merge = condi_merge;
    res.is_gift = is_gift;

    return res;
  }
}
