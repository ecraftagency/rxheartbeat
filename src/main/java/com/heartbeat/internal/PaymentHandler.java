package com.heartbeat.internal;

import com.gateway.model.Payload;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.statics.PaymentData;

import java.util.List;

public class PaymentHandler {
  public static void mobiPaymentSuccess(Session session, Payload payload, boolean online, PaymentData.PaymentDto dto) {
    session.userGameInfo.addTime(dto.time);
    session.userGameInfo.addVipExp(session, dto.vip);
    for (List<Integer> re : dto.reward)
    EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, re);
    if (!online)
      CBSession.getInstance().sync(Integer.toString(payload.sessionId), session, ar -> {});
  }
}