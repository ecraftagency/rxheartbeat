package com.transport;

import com.transport.ws.Echo;
import com.transport.ws.Verify;

public class WSMessage {
  public String cmd;

  public Verify verify;
  public Echo echo;

  public static WSMessage of(String cmd) {
    WSMessage msg = new WSMessage();
    msg.cmd = cmd;
    return msg;
  }
}