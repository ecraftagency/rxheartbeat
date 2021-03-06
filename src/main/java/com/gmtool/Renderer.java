package com.gmtool;

import com.common.Constant;
import com.common.Utilities;
import com.google.gson.reflect.TypeToken;
import com.stdprofile.thrift.StdProfileService;
import com.transport.model.Node;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gmtool.GMTool.eventBus;
import static com.gmtool.GMTool.templateEngine;

public class Renderer implements Handler<RoutingContext> {
  //public static String host = "http://localhost:3000";
  public static String host = "http://18.141.216.52:3000";

  static TTransport transport;
  static TFramedTransport ft;
  static TProtocol protocol;
  public static StdProfileService.Client  thriftClient;

  public Renderer() {
    try {
      transport = new TSocket(Constant.SERVICE.STD_PROFILE_HOST, Constant.SERVICE.STD_PROFILE_PORT);
      ft        = new TFramedTransport(transport);
      protocol  = new TBinaryProtocol(ft);
      transport.open();
      thriftClient = new StdProfileService.Client(protocol);
    }
    catch (Exception e) {
      //inorge
    }
  }
  @Override

  public void handle(RoutingContext ctx) {
    String path = ctx.request().getParam("path");
    ctx.put("host", host);
    switch (path) {
      case "login":
        render("webroot/html/login.ftl", ctx);
        return;
      case "server":
        renderIndex(ctx);
        return;
      case "user":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/user.ftl", ctx);
        return;
      case "mail":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/mail.ftl", ctx);
        return;
      case "config":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/config.ftl", ctx);
        return;
      case "ldb":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/leaderboard.ftl", ctx);
        return;
      case "payment":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/payment.ftl", ctx);
        return;
      case "shop":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/shop.ftl", ctx);
        return;
      case "event":
        ctx.put("nodes", GMTool.getNodes());
        ctx.put("evtType", Arrays.asList("userEvents", "idolEvents", "rankEvents", "groupEvents")); //todo double check template/controller
        render("webroot/html/event.ftl", ctx);
        return;
      case "stats":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/stats.ftl", ctx);
        return;
      case "gift":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/giftcode.ftl", ctx);
        return;
      case "chat":
        ctx.put("nodes", GMTool.getNodes());
        render("webroot/html/neta_chat.ftl", ctx);
        return;
      default:
        ctx.response().setStatusCode(404).end();
    }
  }

  private void render(String templatePath, RoutingContext ctx) {
      templateEngine.render(ctx.data(), templatePath, rar -> {
        if (rar.succeeded()) {
          ctx.response().putHeader("Content-Type", "text/html");
          ctx.response().end(rar.result());
        } else {
          ctx.fail(rar.cause());
        }
      });
  }

  private void renderIndex(RoutingContext ctx) {
    JsonObject jsonMessage = new JsonObject().put("cmd", "getNodes");
    eventBus.request(Constant.SYSTEM_INFO.GATEWAY_EVT_BUS, jsonMessage, ar -> {
      if (ar.succeeded()) {
        JsonObject resp     = (JsonObject) ar.result().body();
        Type listOdNode     = new TypeToken<List<Node>>() {}.getType();
        List<Node> nodes    = Utilities.gson.fromJson(resp.getJsonArray("nodes").toString(), listOdNode);
        String cacheStat    = "";

        try {
          cacheStat = thriftClient.getStat(0);
        }
        catch (Exception e) {
          //ignore
        }
        ctx.put("nodes", nodes);
        ctx.put("cacheStat", cacheStat);
        GMTool.setNodes(nodes);
      }
      else {
        List<Node> nodes = new ArrayList<>();
        ctx.put("nodes", nodes);
        ctx.put("cacheStat", "");
        GMTool.setNodes(nodes);
      }
      render("webroot/html/index.ftl", ctx);
    });
  }
}