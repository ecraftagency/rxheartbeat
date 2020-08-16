package com.transport.model;

import java.io.Serializable;

public class Node implements Serializable {
  public int        id;
  public String     ip;
  public int        port;
  public int        ccu;
  public String     bus;
  public String     name;
  public long       lastSync;

  public static Node of() {
    return new Node();
  }
  public static Node of(int id, String ip, int port, String name, String bus, int ccu) {
    Node node     = new Node();
    node.id       = id;
    node.port     = port;
    node.ip       = ip;
    node.name     = name;
    node.bus      = bus;
    node.ccu      = ccu;
    node.lastSync = System.currentTimeMillis();
    return node;
  }
}