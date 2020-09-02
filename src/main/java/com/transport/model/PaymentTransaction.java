package com.transport.model;

@SuppressWarnings("unused")
public class PaymentTransaction {
  public String transID;
  public int    payAt;
  public long   amount;
  public long   bonus;
  public int    source;
  public long   price;
  public int    useAt;
  public int    payAtPlayingTime;
  public String itemId;

  public static PaymentTransaction of(String id, String itemId, long amount, long bonus, int source, long price, long paidTime) {
    PaymentTransaction ps = new PaymentTransaction();
    ps.transID = id;
    ps.amount = amount;
    ps.bonus = bonus;
    ps.source = source;
    ps.price = price;
    ps.payAt = ps.useAt = (int)(paidTime/1000);
    return ps;
  }

  public void bonusAmount(int amount) {
    if(amount > 0)
      bonus += amount;
  }

  public void bonusRate(float rate) {
    if(rate > 0)
      bonus += (amount + bonus)*rate;
  }

  public long total() {
    return amount + bonus;
  }
}