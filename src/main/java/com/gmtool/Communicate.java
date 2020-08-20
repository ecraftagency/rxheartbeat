package com.gmtool;

import com.common.Constant;
import com.common.Utilities;
import com.google.gson.reflect.TypeToken;
import com.heartbeat.model.Session;
import com.transport.model.Node;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Type;
import java.util.List;
import static com.common.Constant.*;

import static com.gmtool.GMTool.eventBus;

public class Communicate {
  private static DeliveryOptions options = new DeliveryOptions().setSendTimeout(Constant.SYSTEM_INFO.EB_SEND_TIMEOUT);

  public static void findNodeById(int nodeId, Handler<AsyncResult<Node>> nr) {
    JsonObject jsonMessage = new JsonObject().put("cmd", "getNodes");
    eventBus.request(Constant.SYSTEM_INFO.GATEWAY_EVT_BUS, jsonMessage, options, ar -> {
      if (ar.succeeded()) {
        JsonObject resp = (JsonObject) ar.result().body();
        Type listOdNode = new TypeToken<List<Node>>() {}.getType();
        List<Node> nodes = Utilities.gson.fromJson(resp.getJsonArray("nodes").toString(), listOdNode);
        for (Node n : nodes){
          if (n.id == nodeId) {
            nr.handle(Future.succeededFuture(n));
            return;
          }
        }
        nr.handle(Future.failedFuture("node not found " + nodeId));
      }
      else {
        nr.handle(Future.failedFuture("gateway fail to reply"));
      }
    });
  }

  public static void findNode(int sessionId, Handler<AsyncResult<Node>> nr) {
    JsonObject jsonMessage = new JsonObject().put("cmd", "getNodes");
    eventBus.request(Constant.SYSTEM_INFO.GATEWAY_EVT_BUS, jsonMessage, options, ar -> {
      if (ar.succeeded()) {
        JsonObject resp = (JsonObject) ar.result().body();
        Type listOdNode = new TypeToken<List<Node>>() {
        }.getType();
        List<Node> nodes = Utilities.gson.fromJson(resp.getJsonArray("nodes").toString(), listOdNode);
        int serverId = sessionId / SYSTEM_INFO.MAX_USER_PER_NODE;
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

  public static void getSession(int sessionId, Node node, JsonObject ctx, Handler<AsyncResult<Session>> sr) {
    String nodeEb           = node.bus;
    JsonObject jsonMessage  = new JsonObject().put("cmd", "getSession").put("sessionId", sessionId);
    eventBus.request(nodeEb, jsonMessage, options, ar -> {
      if (ar.succeeded()) {
        JsonObject resp = (JsonObject) ar.result().body();
        if (resp.getString("msg").equals("ok")) {
          Session session = Json.decodeValue(resp.getString("session"), Session.class);
          ctx.put("ctx", resp.getString("ctx"));
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

  public static void sendMail(Node node, RoutingContext ctx, Handler<AsyncResult<String>> sr) {
    String nodeEb           = node.bus;
    JsonObject jsonMessage  = new JsonObject().put("cmd", "sendMail")
            .put("mailTitle", ctx.getBodyAsJson().getString("mailTitle"))
            .put("mailContent", ctx.getBodyAsJson().getString("mailContent"))
            .put("mailItems", ctx.getBodyAsJson().getString("mailItems"));

    eventBus.request(nodeEb, jsonMessage, options, ar -> {
      if (ar.succeeded()) {
        JsonObject resp = (JsonObject) ar.result().body();
        if (resp.getString("msg").equals("ok")) {
          sr.handle(Future.succeededFuture(resp.getString("msg")));
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

  public static void injectSession(Node node, RoutingContext ctx, JsonObject _ctx, Handler<AsyncResult<Session>> sr) {
    String strId        = ctx.getBodyAsJson().getString("sessionId");
    String path         = ctx.getBodyAsJson().getString("path");
    String value        = ctx.getBodyAsJson().getString("value");
    int sessionId       = Integer.parseInt(strId);

    String nodeEb           = node.bus;
    JsonObject jsonMessage  = new JsonObject().put("cmd", "inject")
            .put("sessionId", sessionId).put("path", path).put("value", value);
    eventBus.request(nodeEb, jsonMessage, options, ar -> {
      if (ar.succeeded()) {
        JsonObject resp = (JsonObject) ar.result().body();
        if (resp.getString("msg").equals("ok")) {
          Session session = Json.decodeValue(resp.getString("session"), Session.class);
          _ctx.put("ctx", resp.getString("ctx"));
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