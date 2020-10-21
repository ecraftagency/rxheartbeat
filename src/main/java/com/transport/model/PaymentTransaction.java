package com.transport.model;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PaymentTransaction {
  public String               transID;
  public int                  payAt;
  public long                 amount;
  public long                 bonus;
  public int                  source;
  public long                 price;
  public int                  useAt;
  public int                  payAtPlayingTime;
  public String               itemId;
  public List<List<Integer>>  reward;
  public boolean              rewardClaim;
  public String               iapTransId;

  public static PaymentTransaction of(String id, String itemId, long amount, long bonus, int source, long price, int paidTime) {
    PaymentTransaction ps = new PaymentTransaction();
    ps.transID            = id;
    ps.itemId             = itemId;
    ps.amount             = amount;
    ps.bonus              = bonus;
    ps.source             = source;
    ps.price              = price;
    ps.payAt              = ps.useAt = paidTime;
    ps.reward             = new ArrayList<>();
    ps.rewardClaim        = false;
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