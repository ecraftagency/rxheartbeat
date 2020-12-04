package NetaClient;

import NetaClient.message.*;
import NetaClient.protocols.NetaHandler;
import NetaClient.protocols.NetaService;
import com.common.LOG;
import com.common.Utilities;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;


@SuppressWarnings("unused")
public class NetaClientV1 implements NetaService {
  private NetaHandler listener;
  Socket                    socket;

  private String            adminToken  = "54ca583b474538c5f77bab6fa30f1598972db50d";
  private String            uri         = "http://dev.conn1.netalo.vn:2082/data";
  private boolean           isConnect   = false;
  private long              admin_uin   = 281474976981364L;
  private static final int  PRI_GRP     = 1;
  private static final int  COM_GRP     = 2;
  private static final int  PUB_GRP     = 3;

  public NetaClientV1() {
    IO.Options opts     = new IO.Options();
    opts.transports     = new String[]{"websocket"};
    opts.query          = String.format("session=%s", adminToken);
    opts.reconnection   = false;

    try {
      socket            = IO.socket(uri, opts);
      socket.connect();
      socket.on(Socket.EVENT_CONNECT, args -> isConnect = true);
      socket.on(Socket.EVENT_ERROR, args -> {
        isConnect = false;
        LOG.globalException("node", "SocketIO.EVENT_ERROR", "SocketIO.EVENT_ERROR");
      });
      socket.on(Socket.EVENT_DISCONNECT, args -> {
        isConnect = false;
        LOG.globalException("node", "SocketIO.EVENT_DISCONNECT", "SocketIO.EVENT_DISCONNECT");
      });
      socket.on(Socket.EVENT_CONNECT_ERROR, args -> isConnect = false);

      socket.on("create_group", args -> {
        try {
          if (listener == null)
            return;

          JSONObject resp = (JSONObject) args[0];
          CreateGroupResp o = Utilities.gson.fromJson(resp.toString(), CreateGroupResp.class);
          listener.onRespEvt("create_group", o);
        }
        catch (Exception e) {
          LOG.globalException("node", "NetaClientV1.create_group", e);
        }
      });

      socket.on("list_group", args -> {
        try {
          if (listener == null)
            return;

          JSONObject resp = (JSONObject) args[0];
          ListGroupResp o = Utilities.gson.fromJson(resp.toString(), ListGroupResp.class);
          listener.onRespEvt("list_group", o);
        }
        catch (Exception e) {
          LOG.globalException("node", "NetaClientV1.list_group", e);
        }
      });

      socket.on("delete_conversation", args -> {
        try {
          if (listener == null)
            return;

          JSONObject resp = (JSONObject)args[0];
          DeleteGroupResp o = Utilities.gson.fromJson(resp.toString(), DeleteGroupResp.class);
          listener.onRespEvt("delete_conversation", o);
        }
        catch (Exception e) {
          LOG.globalException("node", "NetaClientV1.list_group", e);
        }
      });

      socket.on("update_group", args -> {
        try {
          if (listener == null)
            return;

          JSONObject resp = (JSONObject)args[0];
          UpdateGroupResp o = Utilities.gson.fromJson(resp.toString(), UpdateGroupResp.class);
          listener.onRespEvt("update_group", o);
        }
        catch (Exception e) {
          LOG.globalException("node", "NetaClientV1.update_group", e);
        }
      });
    }
    catch (URISyntaxException e) {
      e.printStackTrace();
      isConnect = false;
    }
  }

  public void setAdminToken(String adminToken) {
    this.adminToken = adminToken;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public void setAdmin_uin(long admin_uin) {
    this.admin_uin = admin_uin;
  }

  public void setListener(NetaHandler listener) {
    this.listener = listener;
  }

  @Override
  public void createGroup(long appId, String groupName, List<Long> occupants) {
    if (!isConnect)
      return;
    CreateGroupReq req = CreateGroupReq.of(appId, groupName, COM_GRP, "", admin_uin, occupants);
    try {
      JSONObject jr = new JSONObject(Utilities.gson.toJson(req));
      socket.emit("create_group", jr);
    }
    catch (Exception e) {
      LOG.globalException("node", "NetaClientV1.create_group", e);
    }
  }

  @Override
  public void listGroup() {
    if (!isConnect)
      return;
    ListGroupReq req = ListGroupReq.of(admin_uin, 0, 100, 2);
    try {
      JSONObject jr = new JSONObject(Utilities.gson.toJson(req));
      socket.emit("list_group", jr);
    }
    catch (Exception e) {
      LOG.globalException("node", "NetaClientV1.list_group", e);
    }
  }

  @Override
  public void updateGroup(long group_id, String name, List<Long> pushIds, List<Long> pullIds) {
    UpdateGroupReq req = UpdateGroupReq.of(group_id, name, "", admin_uin, pushIds, pullIds);
    if (!isConnect)
      return;
    try {
      JSONObject jr = new JSONObject(Utilities.gson.toJson(req));
      socket.emit("update_group", jr);
    }
    catch (Exception e) {
      LOG.globalException("node", "NetaClientV1.update_group", e);
    }
  }

  @Override
  public void deleteGroup(long groupId) {
    if (!isConnect)
      return;
    try {
      socket.emit("delete_conversation");
    }
    catch (Exception e) {
      LOG.globalException("node", "NetaClientV1.delete_conversation", e);
    }
  }

  @Override
  public void setHandler(NetaHandler listener) {
    this.listener = listener;
  }
}