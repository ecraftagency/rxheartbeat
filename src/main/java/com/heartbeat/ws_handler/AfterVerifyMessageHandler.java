package com.heartbeat.ws_handler;

import com.common.Utilities;
import com.heartbeat.model.Session;
import com.transport.WSMessage;
import com.transport.ws.Echo;
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
    resp.echo = new Echo();
    resp.echo.msg = "server echo you!";

    session.wsCtx.writeTextMessage(Json.encode(resp));
  }
}