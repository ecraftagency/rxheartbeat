package com.gmtool;

import com.common.Constant;
import com.transport.model.Node;

import java.util.ArrayList;
import java.util.List;

public class NodeCache {
  private List<Node> nodes;

  private NodeCache() {
    nodes = new ArrayList<>();
  }

  private static NodeCache instance = new NodeCache();

  public static NodeCache inst() {
    return instance;
  }

  public void setNodes(List<Node> nodes) {
    this.nodes.clear();
    this.nodes.addAll(nodes);
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public Node getNodeById(int nodeId) {
    for (Node node : nodes)
      if (node.id == nodeId)
        return node;
    return Node.ofNullObject();
  }

  public Node getNodeBySessionId(int sessionId) {
    int nodeId = sessionId / Constant.SYSTEM_INFO.MAX_USER_PER_NODE;
    for (Node node : nodes)
      if (node.id == nodeId)
        return node;
    return Node.ofNullObject();
  }
}