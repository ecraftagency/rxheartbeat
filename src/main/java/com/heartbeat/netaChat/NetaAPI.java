package com.heartbeat.netaChat;

import com.common.LOG;
import com.heartbeat.HBServer;
import com.heartbeat.db.cb.AbstractCruder;
import com.heartbeat.db.dao.ChatGroupDAO;
import com.transport.NetaGroup;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetaAPI {
  private static String  NETACHAT_ENDPOINT   = "http://dev.conn1.netalo.vn:2082/data";
  private static String  NETACHAT_TOKEN      = "54ca583b474538c5f77bab6fa30f1598972db50d";
  private static String  NETACHAT_UID        = "281474976981364";
  private static String  TEST_GID            = "286075168996800";

  public static Map<String, NetaGroup>  chatGroup;
  private static AbstractCruder<ChatGroupDAO> cbAccess;
  private static String cbKey = "netaGroup";

  static {
    chatGroup = new ConcurrentHashMap<>();
    cbAccess = new AbstractCruder<>(ChatGroupDAO.class, HBServer.rxIndexBucket);
  }

  public static void loadChatGroupsFromDB() {
    ChatGroupDAO dao = cbAccess.load(cbKey);
    if (dao != null && dao.chatGroups != null)
      for (Map.Entry<String, NetaGroup> entry : dao.chatGroups.entrySet())
        chatGroup.put(entry.getKey(), entry.getValue());
  }

  public static void syncChatGroupsToDB() {
    ChatGroupDAO dao = ChatGroupDAO.of(chatGroup);
    cbAccess.sync(cbKey, dao);
  }

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
      socket.on("update_group", new UpdateGroupHandler());
      socket.connect();
    }
    catch (Exception e) {
      LOG.globalException(String.format("node_%d", HBServer.nodeId), "HBServer.start", e);
    }
  }

  public static void addGroup(String groupName, Handler<AsyncResult<String>> handler) {
    if (socket == null) {
      LOG.globalException(String.format("node_%d", HBServer.nodeId), "NetaAPI.joinGroup", "no connection");
      handler.handle(Future.failedFuture("no_connection"));
    }

    JSONObject createGroup = new JSONObject();
    try {
      createGroup.put("type", 1);
      createGroup.put("owner_uin", NETACHAT_UID);
      createGroup.put("name", groupName);
      createGroup.put("avatar_url", "");
      createGroup.put("occupants_uins", Collections.singletonList(NETACHAT_UID));
      createGroup.put("sender_name", "Admin");

      socket.emit("create_group", createGroup).on("create_group", groupResp -> {
        JSONObject resp = (JSONObject)groupResp[0];
        try {
          if (resp.getInt("result") == 0) {
            NetaGroup res = new NetaGroup();
            res.groupId = resp.getJSONObject("group").getString("group_id");
            res.groupName = resp.getJSONObject("group").getString("name");
            chatGroup.put(res.groupId, res);
            syncChatGroupsToDB();
            handler.handle(Future.succeededFuture("ok"));
          }
          else
            handler.handle(Future.failedFuture("unknown_error"));
        }
        catch (Exception e) {
          handler.handle(Future.failedFuture(e.getMessage()));
        }
      });
    } catch (JSONException e) {
      e.printStackTrace();
      handler.handle(Future.failedFuture(e.getMessage()));
    }
  }

  public static void joinGroup(String gid, String uid) {
    if (socket == null) {
      LOG.globalException(String.format("node_%d", HBServer.nodeId), "NetaAPI.joinGroup", "no connection");
      return;
    }

    NetaGroup netaGroup = chatGroup.get(gid);
    if (netaGroup == null) {
      return;
    }

    JSONObject joinGroupMsg = new JSONObject();
    try {
      joinGroupMsg.put("group_id", gid);
      joinGroupMsg.put("name", netaGroup.groupName);
      joinGroupMsg.put("avatar_url", "");
      joinGroupMsg.put("push_all", Collections.singletonList(uid));
      socket.emit("update_group", joinGroupMsg);
      LOG.info(String.format("node_%d", HBServer.nodeId), "NetaAPI.joinGroup",
              String.format("uid:%s - gid:%s", uid,gid));
    }
    catch (JSONException e) {
      LOG.globalException(String.format("node_%d", HBServer.nodeId), "NetaAPI.joinGroup", e);
    }
  }


  public static class UpdateGroupHandler implements Emitter.Listener {
    public Handler<AsyncResult<String>> handler;
    @Override
    public void call(Object... args) {
      JSONObject resp = (JSONObject)args[0];
      if (resp != null)
        LOG.info(String.format("node_%d", HBServer.nodeId), "NetaEvent:update_group", resp.toString());
    }
  }
}