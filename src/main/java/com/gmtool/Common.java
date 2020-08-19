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
import io.vertx.ext.web.RoutingContext;

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
        if (resp.getString("msg").equals("ok")) {
          Session session = Json.decodeValue(resp.getString("session"), Session.class);
          sr.handle(Future.succeededFuture(session));
        }
        else {
          sr.handle(Future.failedFuture(resp.getString("msg")));
        }
      }
      else {
        sr.handle(Future.failedFuture(ar.cause().getMessage()));
      }
    });
  }

  public static void injectSession(Node node, RoutingContext ctx, Handler<AsyncResult<Session>> sr) {
    String strId        = ctx.getBodyAsJson().getString("sessionId");
    String path         = ctx.getBodyAsJson().getString("path");
    String value        = ctx.getBodyAsJson().getString("value");
    int sessionId       = Integer.parseInt(strId);

    String nodeEb           = node.bus;
    JsonObject jsonMessage  = new JsonObject().put("cmd", "inject")
            .put("sessionId", sessionId).put("path", path).put("value", value);
    eventBus.request(nodeEb, jsonMessage, ar -> {
      if (ar.succeeded()) {
        JsonObject resp = (JsonObject) ar.result().body();
        if (resp.getString("msg").equals("ok")) {
          Session session = Json.decodeValue(resp.getString("session"), Session.class);
          sr.handle(Future.succeededFuture(session));
        }
        else {
          sr.handle(Future.failedFuture(resp.getString("msg")));
        }
      }
      else {
        sr.handle(Future.failedFuture(ar.cause().getMessage()));
      }
    });
  }
}