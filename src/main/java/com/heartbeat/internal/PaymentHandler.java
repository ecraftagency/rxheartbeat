package com.heartbeat.internal;

import com.gateway.model.Payload;
import com.heartbeat.db.cb.CBSession;
import com.heartbeat.model.Session;

public class PaymentHandler {
  public static void mobiPaymentSuccess(Session session, Payload payload, boolean online) {
    session.userGameInfo.time += payload.gold;

    if (!online)
      CBSession.getInstance().sync(Integer.toString(payload.sessionId), session, ar -> {});
  }
}