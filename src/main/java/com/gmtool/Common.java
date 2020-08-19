package com.gmtool;

import com.common.Constant;
import com.common.Utilities;
import com.google.gson.reflect.TypeToken;
import com.heartbeat.model.Session;
import com.transport.model.Node;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Type;
import java.util.List;

import static com.gmtool.GMTool.eventBus;

public class Common {
  public static void findNode(int sessionId, Handler<AsyncResult<Node>> nr) {
    JsonObject jsonMessage = new JsonObject().put("cmd", "getNodes");
    eventBus.request(Constant.SYSTEM_INFO.GATEWAY_EVT_BUS, jsonMessage, ar -> {
      if (ar.succeeded()) {
        JsonObject resp = (JsonObject) ar.result().body();
        Type listOdNode = new TypeToken<List<Node>>() {
        }.getType();
        List<Node> nodes = Utilities.gson.fromJson(resp.getJsonArray("nodes").toString(), listOdNode);
        int serverId = sessionId / 1000000;
        for (Node n : nodes){
          if (n.id == serverId) {
            nr.handle(Future.succeededFuture(n));
            return;
          }
        }
        nr.handle(Future.failedFuture("node not found " + sessionId));
      }
      else {
        nr.handle(Future.failedFuture("gateway fail to reply"));
      }
    });
  }

  public static void getSession(int sessionId, Node node, Handler<AsyncResult<Session>> sr) {
    String nodeEb           = node.bus;
    JsonObject jsonMessage  = new JsonObject().put("cmd", "getSession").put("sessionId", sessionId);
    eventBus.request(nodeEb, jsonMessage, ar -> {
      if (ar.succeeded()) {
        JsonObject resp = (JsonObject) ar.result().body();
        Session session = Json.decodeValue(resp.toString(), Session.class);
        sr.handle(Future.succeededFuture(session));
      }
      else {
        sr.handle(Future.failedFuture(ar.cause().getMessage()));
      }
    });
  }
}