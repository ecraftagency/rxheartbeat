package com.gateway.controller;

import com.common.Constant;
import com.common.GlobalVariable;
import com.common.LOG;
import com.common.Utilities;
import com.gateway.NodePool;
import com.gateway.model.Payload;
import com.transport.model.Node;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static com.common.Constant.PAYMENT.SECRET;
import static com.gateway.HBGateway.eventBus;

public class IAPPayment implements Handler<RoutingContext> {
  private static DeliveryOptions deliOps  = new DeliveryOptions().setSendTimeout(Constant.SYSTEM_INFO.EB_SEND_TIMEOUT);

  @Override
  public void handle(RoutingContext ctx) {
    try {
      String    id100D       = ctx.request().getParam("userid");
      int       sessionId    = Integer.parseInt(ctx.request().getParam("roleid"));
      int       nodeId       = Integer.parseInt(ctx.request().getParam("server_id"));
      String    orderId      = ctx.request().getParam("order_id");
      String    iapTransId   = ctx.request().getParam("payload");
      String    itemId       = ctx.request().getParam("item_id");
      long      money        = Long.parseLong(ctx.request().getParam("money"));
      long      gold         = Long.parseLong(ctx.request().getParam("gold"));
      int       time         = Integer.parseInt(ctx.request().getParam("time"));
      String    sign         = ctx.request().getParam("sign");
      String    verifySign   = Utilities.md5Encode(
              GlobalVariable.stringBuilder.get()
                      .append(id100D)
                      .append(sessionId)
                      .append(nodeId)
                      .append(orderId)
                      .append(iapTransId)
                      .append(itemId)
                      .append(money)
                      .append(gold)
                      .append(time)
                      .append(SECRET).toString()
      );

      if (!sign.equals(verifySign)) {
        response(ctx, -7, "Sign error");
        LOG.paymentException(String.format("Sign Error. sessionId:%d - itemId:%s - orderId:%s", sessionId, itemId, orderId));
        return;
      }

      int curSec = (int)(System.currentTimeMillis()/1000);
      if (curSec - time > 60*3) {
        response(ctx, -2, "Expire time");
        LOG.paymentException(String.format("Expire Time. sessionId:%d - itemId:%s - orderId:%s", sessionId, itemId, orderId));
        return;
      }

      Node node = NodePool.getNodeFromPool(nodeId);
      if (node == null) {
        response(ctx, -1, "Server not found");
        return;
      }

      JsonObject localReq = new JsonObject();
      localReq.put("cmd", "iapexchange");
      localReq.put("payload", Json.encode(Payload.of(id100D, sessionId, nodeId, orderId, iapTransId, itemId, money, gold, time)));

      eventBus.request(node.bus, localReq, deliOps, far -> {
        if (far.succeeded()) {
          JsonObject localResp = (JsonObject) far.result().body();
          try {
            response(ctx, localResp.getInteger("status"), localResp.getString("msg"));
          }
          catch (Exception e) {
            response(ctx, -9, "Exchange fail");
            LOG.paymentException(e);
          }
        }
        else {
          response(ctx, -9, "Exchange fail");
          LOG.paymentException(far.cause().getCause());
        }
      });
    }
    catch (Exception e) {
      response(ctx, -9, "Exchange fail");
      LOG.paymentException(e);
    }
  }

  private void response(RoutingContext ctx, int code, String message) {
    JsonObject resp     = new JsonObject();
    resp.put("status", code);
    resp.put("msg", message);
    ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
  }
}
