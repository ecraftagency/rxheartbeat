package com.gateway.controller;

import com.transport.model.Node;
import com.gateway.NodePool;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.List;


public class NodeController implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    List<Node> availableNodes = NodePool.getNodes();
    ctx.response().setStatusCode(200).end(Json.encode(availableNodes));
  }
}
