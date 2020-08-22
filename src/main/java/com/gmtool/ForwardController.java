package com.gmtool;

import com.common.Constant;
import com.transport.model.Node;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import static com.gmtool.GMTool.eventBus;

// client request -> determining node -> fwd request => process => transform => response -> response
public class ForwardController implements Handler<RoutingContext> {
  private static DeliveryOptions options = new DeliveryOptions().setSendTimeout(Constant.SYSTEM_INFO.EB_SEND_TIMEOUT);

  @Override
  public void handle(RoutingContext ctx) {
    int nodeId          = getNodeId(ctx);
    Node node           = GMTool.getNodeById(nodeId);
    forwardRequest(node, ctx);
  }

  private void forwardRequest(Node node, RoutingContext ctx) {
    eventBus.request(node.bus, ctx.getBodyAsJson(), options, far -> {
      if (far.succeeded()) {
        JsonObject resp = (JsonObject) far.result().body();
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
      else {
        JsonObject resp = new JsonObject();
        resp.put("msg", far.cause().getMessage());
        ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
      }
    });
  }

  private int getNodeId(RoutingContext ctx) {
    int    nodeId, sessionId;

    try {
      String strSessionId  = ctx.getBodyAsJson().getString("sessionId");
      String strNodeId     = ctx.getBodyAsJson().getString("serverId");

      if (strSessionId != null) {
        sessionId      = Integer.parseInt(strSessionId);
        nodeId         = sessionId/ Constant.SYSTEM_INFO.MAX_USER_PER_NODE;
      }
      else if (strNodeId != null){
        nodeId         = Integer.parseInt(strNodeId);
      }
      else {
        nodeId         = -1;
      }
    }
    catch (Exception e) {
      nodeId = -1;
    }

    return nodeId;
  }
}