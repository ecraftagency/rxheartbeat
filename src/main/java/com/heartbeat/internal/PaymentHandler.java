package com.heartbeat.internal;

import com.common.Constant;
import com.gateway.model.Payload;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.PaymentData;
import com.transport.model.PaymentTransaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static com.common.Constant.USER_PAYMENT.*;

public class PaymentHandler {
  public static void _100DPaymentSuccess(Session session, Payload payload, boolean online, PaymentData.PaymentDto dto) {
    PaymentTransaction trans = PaymentTransaction.of(payload.orderId, payload.itemId, payload.gold, 0, PAYMENT_CHANNEL_100D, payload.money, payload.time);
    if (session.userPayment.firstPaying()) {
      handleFirstPayment();
    }


    session.userGameInfo.addTime(dto.time);
    session.userGameInfo.addVipExp(session, dto.vip);
    for (List<Integer> re : dto.reward) {
      EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, re);
    }
    session.effectResults.clear();

    trans.reward.addAll(dto.reward);
    session.userPayment.addHistory(trans);

    if (session.userEvent != null)
      session.userEvent.addEventRecord(Constant.COMMON_EVENT.VIP_INCR_EVT_ID, dto.vip);
    if (!online)
      CBSession.getInstance().sync(Integer.toString(payload.sessionId), session, ar -> {});
  }

  public static void handleFirstPayment() {
    //todo not implemented
  }
}