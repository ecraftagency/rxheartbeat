package com.gateway.controller;

import com.common.Constant;
import com.common.GlobalVariable;
import com.common.LOG;
import com.common.Utilities;
import com.gateway.NodePool;
import com.transport.model.Node;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static com.common.Constant.PAYMENT.*;
import static com.common.Constant.PAYMENT.NOT_FOUND_STATUS_CODE;
import static com.gateway.HBGateway.eventBus;

public class NetCardExchange implements Handler<RoutingContext> {
  private static DeliveryOptions deliOps  = new DeliveryOptions().setSendTimeout(Constant.SYSTEM_INFO.EB_SEND_TIMEOUT);
  private static JsonArray blankData      = new JsonArray();

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String _100dId      = ctx.request().getParam("userid");
      int    sessionId    = Integer.parseInt(ctx.request().getParam("roleid"));
      int    nodeId       = Integer.parseInt(ctx.request().getParam("server_id"));
      int    time         = Integer.parseInt(ctx.request().getParam("time"));
      int    amount       = Integer.parseInt(ctx.request().getParam("amount"));
      String sign         = ctx.request().getParam("sign");

      String verifySign   = Utilities.md5Encode(
              GlobalVariable.stringBuilder.get()
                      .append(_100dId)
                      .append(sessionId)
                      .append(nodeId)
                      .append(amount)
                      .append(time)
                      .append(SECRET).toString()
      );

      if (!sign.equals(verifySign)) {
        response(ctx, "Sign Wrong", SIGN_WRONG_STATUS_CODE, blankData);
        return;
      }

      int curSec = (int)(System.currentTimeMillis()/1000);
      if (curSec - time > 60*3L) { //3 minutes
        response(ctx, "expire time", EXPIRE_TIME_STATUS_CODE, blankData);
      }

      Node node = NodePool.getNodeFromPool(nodeId);
      if (node == null) {
        response(ctx, "server not found", NOT_FOUND_STATUS_CODE, blankData);
        return;
      }

      JsonObject localReq = new JsonObject();
      localReq.put("cmd", "nc_exchange");
      localReq.put("sessionId", sessionId);
      localReq.put("amount", amount);

      eventBus.request(node.bus, localReq, deliOps, far -> {
        if (far.succeeded()) {
          JsonObject localResp = (JsonObject) far.result().body();
          try {
            response(ctx, localResp.getString("msg"), localResp.getInteger("status"), blankData);
          }
          catch (Exception e) {
            response(ctx, "Exchange fail", -9, blankData);
            LOG.paymentException("Gateway", "NCExchangeHandler", e);
          }
        }
        else {
          response(ctx, "Exchange fail", -9, blankData);
          LOG.paymentException("Gateway", "NCExchangeHandler", far.cause().getCause());
        }
      });
    }
    catch (Exception e) {
      response(ctx, "Exchange fail", -9, blankData);
      LOG.paymentException("Gateway", "NCExchangeHandler", e);
    }
  }

  private void response(RoutingContext ctx, String msg, int status, JsonArray data) {
    JsonObject resp     = new JsonObject();
    resp.put("message", msg);
    resp.put("status", status);
    resp.put("data", data);
    ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
  }
}