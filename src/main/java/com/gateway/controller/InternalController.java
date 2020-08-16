package com.gateway.controller;

import com.transport.model.Node;
import com.gateway.NodePool;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalController implements Handler<Message<JsonObject>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(InternalController.class);

  @Override
  public void handle(Message<JsonObject> ctx) {
    try {
      JsonObject json = ctx.body();
      String  cmd     = json.getString("cmd");

      if (cmd.equals("ping")) {
        int     nodeId      = json.getInteger("nodeId");
        String  nodeIp      = json.getString("nodeIp");
        int     nodePort    = json.getInteger("nodePort");
        String  nodeName    = json.getString("nodeName");
        String  nodeBus     = json.getString("nodeBus");
        int     nodeCcu     = json.getInteger("nodeCcu");

        Node node       = NodePool.getNodeFromPool(nodeId);
        if (node != null) {
          node.ip       = nodeIp;
          node.port     = nodePort;
          node.ccu      = nodeCcu;
          node.bus      = nodeBus;
          node.name     = nodeName;
          node.lastSync = System.currentTimeMillis();
        }
        else {
          Node newNode  = Node.of(nodeId, nodeIp, nodePort, nodeName, nodeBus, nodeCcu);
          NodePool.addNode(newNode);
          LOGGER.info(
            String.format("new node added, nodeId: %d, nodeIp: %s, nodePort: %d",
            nodeId, nodeIp, nodePort));
        }
      }
    }
    catch (Exception e) {
      LOGGER.error(e.getMessage());
    }

  }
}