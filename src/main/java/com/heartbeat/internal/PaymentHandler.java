package com.heartbeat.internal;

import com.common.Constant;
import com.common.LOG;
import com.gateway.model.Payload;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.Session;
import com.statics.PaymentData;
import com.transport.model.PaymentTransaction;

import static com.common.Constant.USER_PAYMENT.*;

public class PaymentHandler {
  public static final String MONTH_GC_ID = "phv001.107";

  public static void _100DPaymentSuccess(Session session, Payload payload, boolean online, PaymentData.PaymentDto dto) {
    PaymentTransaction trans = PaymentTransaction.of(payload.orderId, payload.itemId, payload.gold, 0, PAYMENT_CHANNEL_100D, payload.money, payload.time);

    session.userGameInfo.addTime(dto.time);
    session.userGameInfo.addVipExp(session, dto.vip);

    if (payload.itemId.equals(MONTH_GC_ID)) {
      String res = session.userRollCall.addGiftCard(session, payload.time, 2);
      if (!res.equals("ok")) {
        LOG.globalException("node", "paymentMonthlyGC", res);
      }
    }

    if (session.userEvent != null)
      session.userEvent.addEventRecord(Constant.COMMON_EVENT.VIP_INCR_EVT_ID, dto.vip);

    if (session.userPayment != null && session.userPayment.firstPaying()) {
      handleFirstPayment(session);
    }

    trans.reward.addAll(dto.reward);
    session.userPayment.addHistory(trans);

    if (!online)
      CBSession.getInstance().sync(Integer.toString(payload.sessionId), session, ar -> {});
  }

  public static void IAPPaymentSuccess(Session session, Payload payload, boolean online, PaymentData.PaymentDto dto) {
    PaymentTransaction trans  = PaymentTransaction.of(payload.orderId, payload.itemId, payload.gold, 0, PAYMENT_CHANNEL_GOOGLE_IAP, payload.money, payload.time);
    trans.iapTransId          = payload.iapTransId;

    session.userGameInfo.addTime(dto.time);
    session.userGameInfo.addVipExp(session, dto.vip);

    if (payload.itemId.equals(MONTH_GC_ID)) {
      String res = session.userRollCall.addGiftCard(session, payload.time, 2);
      if (!res.equals("ok")) {
        LOG.globalException("node", "paymentMonthlyGC", res);
      }
    }

    if (session.userEvent != null)
      session.userEvent.addEventRecord(Constant.COMMON_EVENT.VIP_INCR_EVT_ID, dto.vip);

    if (session.userPayment != null && session.userPayment.firstPaying()) {
      handleFirstPayment(session);
    }

    trans.reward.addAll(dto.reward);
    session.userPayment.addHistory(trans);

    if (!online)
      CBSession.getInstance().sync(Integer.toString(payload.sessionId), session, ar -> {});
  }

  public static void handleFirstPayment(Session session) {
    try {
      session.userRollCall.isPaidUser = true;
    }
    catch (Exception e) {
      LOG.paymentException(e);
    }
  }
}