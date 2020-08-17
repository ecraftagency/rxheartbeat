package com.gateway;

import com.common.Constant;
import com.common.LOG;
import com.transport.model.Node;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class NodePool {
  static ConcurrentHashMap<Integer, Node> pool = new ConcurrentHashMap<>();

  public static Node getNodeFromPool(int nodeId) {
    return pool.get(nodeId);
  }

  public static void removeNode(int nodeId) {
    pool.remove(nodeId);
  }

  public static void addNode(Node node) {
    if (node != null && pool.get(node.id) == null) {
      pool.put(node.id, node);
    }
  }

  public static List<Node> getNodes() {
    List<Node> nodes  = new ArrayList<>();
    long curMs        = System.currentTimeMillis();
    Enumeration<Integer> e = pool.keys();
    while(e.hasMoreElements()) {
      Integer nodeId = e.nextElement();
      try {
        Node node = pool.get(nodeId);
        if (node != null && curMs - node.lastSync < Constant.SYSTEM_INFO.NODE_HEARTBEAT_INTERVAL) {
          nodes.add(node);
        }
      }
      catch (Exception ex) {
        LOG.globalException(ex);
      }
    }
    return nodes;
  }
}