package com.heartbeat.model.data;

import com.common.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.transport.model.PaymentTransaction;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPayment {
  public ArrayList<PaymentTransaction> history;
  public int lastPaymentCheck;

  public static UserPayment ofDefault() {
    UserPayment up = new UserPayment();
    up.history = new ArrayList<>();
    return up;
  }

  public void reBalance() {
    if (history == null)
      history = new ArrayList<>();
  }

  public List<PaymentTransaction> getUncheckTrans() {
    int second = (int)(System.currentTimeMillis()/1000);
    List<PaymentTransaction> res = new ArrayList<>();
    for (PaymentTransaction ps : history) {
      if (ps.payAt > lastPaymentCheck)
        res.add(ps);
    }
    this.lastPaymentCheck = second;
    return res;
  }

  public boolean addHistory(PaymentTransaction transaction) {
    if(transaction != null) {
      if(history == null)
        history = new ArrayList<>();
      for(PaymentTransaction trans : history)
        if(trans.transID.equals(transaction.transID))
          return false;
      history.add(transaction);
      return true;
    }
    return false;
  }

  public boolean isOrderLoop(String orderId) {
    if(orderId != null) {
      if(history == null)
        history = new ArrayList<>();
      for(PaymentTransaction trans : history)
        if(trans.transID.equals(orderId))
          return true;
      return false;
    }
    return false;
  }

  public boolean firstPaying() {
    if(history == null)
      history = new ArrayList<>();
    return history.isEmpty();
  }

  public int countTransactionsAfter(int time) {
    int count = 0;
    for(PaymentTransaction trans : history)
      if(trans.payAt >= time)
        count++;
    return count;
  }

  public int lookupChanel(List<Integer> sources) {
    if (history == null || history.isEmpty())
      return Constant.USER_PAYMENT.PAYMENT_CHANNEL_NONE;

    if (sources != null && !sources.isEmpty()) {
      for (PaymentTransaction trans : history) {
        if (trans != null && sources.contains(trans.source)) {
          return trans.source;
        }
      }
    }
    return Constant.USER_PAYMENT.PAYMENT_CHANNEL_UNKNOWN;
  }
}