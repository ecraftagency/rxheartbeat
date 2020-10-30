package com.heartbeat.ws_handler;

import com.common.Utilities;
import com.heartbeat.model.Session;
import com.heartbeat.model.SessionPool;
import com.transport.WSMessage;
import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;

public class PreVerifyMessageHandler implements Handler<ServerWebSocket> {
  @Override
  public void handle(ServerWebSocket ctx) {
    ctx.textMessageHandler(string -> {
      try {
        WSMessage msg = Utilities.gson.fromJson(string, WSMessage.class);
        if (msg.cmd.equals("verify")) {
          handleVerify(ctx, msg);
        }
        else {
          ctx.close();
        }
      }
      catch (Exception e) {
        ctx.close();
      }
    });
  }

  private void handleVerify(ServerWebSocket ctx, WSMessage msg) {
    Session session = SessionPool.getSessionFromPool(msg.verify.sessionId);
    if (session != null) {
      session.wsCtx = ctx;
      ctx.textMessageHandler(new AfterVerifyMessageHandler(session));
    }
    else {
      System.out.println("connection close");
      ctx.close();
    }
  }
}