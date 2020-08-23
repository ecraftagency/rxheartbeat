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
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import static com.common.Constant.PAYMENT.*;
import static com.gateway.HBGateway.*;

public class GetRoleController implements Handler<RoutingContext> {
  private static DeliveryOptions deliOps  = new DeliveryOptions().setSendTimeout(Constant.SYSTEM_INFO.EB_SEND_TIMEOUT);
  private static JsonObject blankData     = new JsonObject();
  @Override
  public void handle(RoutingContext ctx) {
    try {
      String userId       = ctx.request().getParam("userid");
      int nodeId          = Integer.parseInt(ctx.request().getParam("server_id"));
      long time           = Long.parseLong(ctx.request().getParam("time"));
      String sign         = ctx.request().getParam("sign");
      String verifySign   = Utilities.md5Encode(
              GlobalVariable.stringBuilder.get()
              .append(userId)
              .append(nodeId)
              .append(time)
              .append(SECRET).toString()
      );

      if (!sign.equals(verifySign)) {
        response(ctx, "wrong sign", SIGN_WRONG_STATUS_CODE, blankData);
        return;
      }

      if (System.currentTimeMillis() - time > 1000*60*3L) { //3 minutes
        response(ctx, "expire time", EXPIRE_TIME_STATUS_CODE, blankData);
      }

      Node node = NodePool.getNodeFromPool(nodeId);
      if (node == null) {
        response(ctx, "server not found", NOT_FOUND_STATUS_CODE, blankData);
        return;
      }

      JsonObject localReq = new JsonObject();
      localReq.put("cmd", "getRole100D");
      localReq.put("100DID", userId);
      eventBus.request(node.bus, localReq, deliOps, far -> {
        if (far.succeeded()) {
          JsonObject localResp = (JsonObject) far.result().body();
          try {
            if (localResp.getString("msg").equals("ok")) {
              response(ctx, "success", 0, localResp.getJsonObject("getRoleData"));
            }
            else {
              response(ctx, "user not found", NOT_FOUND_STATUS_CODE, blankData);
            }
          }
          catch (Exception e) {
            response(ctx, "exception caught", NOT_FOUND_STATUS_CODE, blankData);
            LOG.paymentException(e);
          }
        }
        else {
          response(ctx, "node take too long to response", NOT_FOUND_STATUS_CODE, blankData);
        }
      });
    }
    catch (Exception e) {
      response(ctx, "exception caught", NOT_FOUND_STATUS_CODE, blankData);
      LOG.paymentException(e);
    }
  }

  private void response(RoutingContext ctx, String msg, int status, JsonObject data) {
    JsonObject resp     = new JsonObject();
    resp.put("message", msg);
    resp.put("status", status);
    resp.put("data", data);
    ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
  }
}