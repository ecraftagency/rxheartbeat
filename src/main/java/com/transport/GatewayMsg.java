package com.transport;

import com.transport.model.Node;

import java.util.List;

public class GatewayMsg {
  public List<Node> availableNodes;
  public String     appStore;
  public String     ggPlay;
  public int        serverVersion;
  public int        minClientVersion;
  public String     adminEmail;
  public String     fanPage;
  public String     refCodeLink;
}