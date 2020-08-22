package com.transport.model;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Node implements Serializable {
  public int        id;
  public String     ip;
  public int        port;
  public int        ccu;
  public String     bus;
  public String     name;
  public long       lastSync;

  private Node() {
    id        = 0;
    ip        = "blank_ip";
    port      = 0;
    ccu       = 0;
    bus       = "null_bus";
    name      = "blank_name";
    lastSync  = 0;
  }

  public int getId() {
    return id;
  }

  public int getCcu() {
    return ccu;
  }

  public String getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  public String getBus() {
    return bus;
  }

  public String getName() {
    return name;
  }

  public long getLastSync() {
    return lastSync;
  }

  public static Node ofNullObject() {
    return new Node();
  }
  public static Node of(int id, String ip, int port, String name, String bus, int ccu) {
    Node node     = new Node();
    node.id       = id;
    node.port     = port;
    node.ip       = ip;
    node.name     = name;
    node.bus      = bus;
    node.ccu = ccu;
    node.lastSync = System.currentTimeMillis();
    return node;
  }
}