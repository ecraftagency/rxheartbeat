package com.heartbeat.model.data;

import com.common.Constant;
import com.transport.model.PaymentTransaction;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UserPayment {
  public ArrayList<PaymentTransaction> history;

  public static UserPayment ofDefault() {
    UserPayment up = new UserPayment();
    up.history = new ArrayList<>();
    return up;
  }

  public void reBalance() {
    if (history == null)
      history = new ArrayList<>();
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

  public boolean isFirstPaying() {
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

  public int getChannel(List<Integer> sources) {
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