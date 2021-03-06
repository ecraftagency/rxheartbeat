package com.heartbeat.controller;

import com.common.LOG;
import com.common.Utilities;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.statics.PaymentData;
import com.transport.ExtMessage;
import com.transport.model.PaymentTransaction;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class PaymentController implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    String cmd          = "";
    try {
      cmd               = ctx.getBodyAsJson().getString("cmd");
      String strUserId  = ctx.user().principal().getString("username");
      int lastIssued    = ctx.user().principal().getInteger("issueTime");
      Session session   = SessionPool.getSessionFromPool(Integer.parseInt(strUserId));

      if (session != null && session.userProfile.lastLogin == lastIssued) {
        ExtMessage resp;
        switch (cmd) {
          case "getPaymentPackage":
            resp = processGetPaymentPackage();
            break;
          case "paymentHistory":
            resp = processGetPaymentHistory(session);
            break;
          default:
            resp = ExtMessage.media();
            resp.msg = "unknown_cmd";
            break;
        }

        resp.cmd            = cmd;
        resp.timeChange     = session.userGameInfo.timeChange;
        resp.userRemainTime = session.userGameInfo.remainTime();

        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
        session.effectResults.clear();
        session.userGameInfo.timeChange = false;
      }
      else {
        ctx.response().setStatusCode(401).end();
      }
    }
    catch (Exception e) {
      ctx.response().setStatusCode(404).end();
      LOG.globalException("node", cmd, e);
    }
  }

  private ExtMessage processGetPaymentPackage() {
    ExtMessage resp = ExtMessage.payment();
    resp.data.extObj = Utilities.gson.toJson(PaymentData.paymentDtoMap);
    return resp;
  }

  private ExtMessage processGetPaymentHistory(Session session) {
    ExtMessage resp               = ExtMessage.payment();
    List<PaymentTransaction> res  = session.userPayment.getUncheckTrans();

    for (PaymentTransaction trans : res) {
      if (!trans.rewardClaim) {
        for (List<Integer> re : trans.reward) {
          EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, re);
        }
        trans.rewardClaim = true;
      }
    }

    resp.effectResults            = session.effectResults;
    resp.data.extObj              = Json.encode(res);
    resp.data.gameInfo            = session.userGameInfo;
    return resp;
  }
}
