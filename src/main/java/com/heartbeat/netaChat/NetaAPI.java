package com.heartbeat.netaChat;

import com.common.LOG;
import com.heartbeat.HBServer;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

public class NetaAPI {
  public static String  NETACHAT_ENDPOINT   = "http://dev.conn1.netalo.vn:2082/data";
  public static String  NETACHAT_TOKEN      = "54ca583b474538c5f77bab6fa30f1598972db50d";
  public static String  TEST_GID            = "286075168996800";

  private static Socket socket;

  public static void initNetaChat() {
    try {
      IO.Options opts = new IO.Options();
      opts.transports = new String[]{"websocket"};
      opts.query      = String.format("session=%s", NETACHAT_TOKEN);
      opts.reconnection = false;

      socket = IO.socket(NETACHAT_ENDPOINT, opts);
      socket  .on(Socket.EVENT_CONNECT, ca -> System.out.println("connected"))
              .on(Socket.EVENT_ERROR, ea -> {
                socket = null;
                LOG.globalException(String.format("node_%d", HBServer.nodeId), "NetaAPI.initNetaChat", "Error on Connection");
              });
      socket.connect();
    }
    catch (Exception e) {
      LOG.globalException(String.format("node_%d", HBServer.nodeId), "HBServer.start", e);
    }
  }

  public static void joinGroup(String uid) {
    if (socket == null) {
      LOG.globalException(String.format("node_%d", HBServer.nodeId), "NetaAPI.joinGroup", "no connection");
      return;
    }
    JSONObject joinGroupMsg = new JSONObject();
    try {
      joinGroupMsg.put("group_id", TEST_GID);
      joinGroupMsg.put("name", "GroupTest 101");
      joinGroupMsg.put("avatar_url", "");
      joinGroupMsg.put("push_all", Collections.singletonList(uid));
      socket.emit("update_group", joinGroupMsg).on("update_group", joinGroupResp -> {
        JSONObject resp = (JSONObject)joinGroupResp[0];
        LOG.info(String.format("node_%d", HBServer.nodeId), "NetaAPI.joinGroup", resp.toString());
      });
    }
    catch (JSONException e) {
      e.printStackTrace();
    }
  }
}