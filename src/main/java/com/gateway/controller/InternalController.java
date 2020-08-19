package com.gateway.controller;

import com.common.LOG;
import com.transport.model.Node;
import com.gateway.NodePool;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import java.util.List;

public class InternalController implements Handler<Message<JsonObject>> {
  @Override
  public void handle(Message<JsonObject> ctx) {
    try {
      JsonObject json = ctx.body();
      String  cmd     = json.getString("cmd");

      switch (cmd) {
        case "ping":
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
            node.ccu = nodeCcu;
            node.bus      = nodeBus;
            node.name     = nodeName;
            node.lastSync = System.currentTimeMillis();
          }
          else {
            Node newNode  = Node.of(nodeId, nodeIp, nodePort, nodeName, nodeBus, nodeCcu);
            NodePool.addNode(newNode);
            LOG.console(
                    String.format("new node added, nodeId: %d, nodeIp: %s, nodePort: %d",
                            nodeId, nodeIp, nodePort));
          }
          break;
        case "getNodes":
          List<Node> availableNodes = NodePool.getNodes();
          JsonObject resp = new JsonObject();
          resp.put("nodes", availableNodes);
          ctx.reply(resp);
          break;
        default:break;
      }
    }
    catch (Exception e) {

      LOG.globalException(e.getMessage());
    }

  }
}