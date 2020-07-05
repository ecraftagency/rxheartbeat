package com.heartbeat.model.data;

@SuppressWarnings("unused")
public class PaymentTransaction {
  public String transID;
  public int payAt;
  public int amount;
  public int bonus;
  public int source;
  public int price;
  public int useAt;
  public int payAtPlayingTime;
  public PaymentTransaction(String id, int amout, int bonus, int source, int price, int second) {
    transID = id;
    this.amount = amout;
    this.bonus = bonus;
    this.source = source;
    this.price = price;
    payAt = useAt = second;
  }

  public void bonusAmount(int amount) {
    if(amount > 0)
      bonus += amount;
  }

  public void bonusRate(float rate) {
    if(rate > 0)
      bonus += (amount + bonus)*rate;
  }

  public int total() {
    return amount + bonus;
  }
}
