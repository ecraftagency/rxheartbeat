package com.heartbeat.ws_handler;

import com.common.Utilities;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.WSMessage;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;

public class AfterVerifyMessageHandler implements Handler<String> {
  private Session session;
  private WSMessage resp;
  public AfterVerifyMessageHandler(Session session) {
    this.session = session;
    this.resp = WSMessage.of("");
  }
  @Override
  public void handle(String event) {
    WSMessage msg = Utilities.gson.fromJson(event, WSMessage.class);
    String cmd = msg.cmd;
    if (cmd != null) {
      long curMs = System.currentTimeMillis();
      switch (cmd) {
        case "echo":
          handleChatCmd(session, cmd, msg, curMs);
          return;
        default:
          break;
      }
    }
  }

  private void handleChatCmd(Session session, String cmd, WSMessage msg, long curMs) {
    resp.cmd = cmd;
    resp.echo = msg.echo;
    for (Session s : SessionPool.pool.values())
      if (s.wsCtx != null && !s.wsCtx.isClosed()) {
        s.wsCtx.writeTextMessage(Json.encode(resp));
      }
  }
}