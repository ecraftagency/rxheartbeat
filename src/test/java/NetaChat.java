import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;

public class NetaChat {

  public static void main(String[] args) throws URISyntaxException, InterruptedException, JSONException {

    IO.Options opts = new IO.Options();
    opts.transports = new String[]{"websocket"};
    opts.query      = "session=54ca583b474538c5f77bab6fa30f1598972db50d";
    opts.reconnection = false;

    Socket socket = IO.socket("http://dev.conn1.netalo.vn:2082/data", opts);
    socket
    .on(Socket.EVENT_CONNECT, args1 -> System.out.println("connected"))
    .on(Socket.EVENT_ERROR, args12 -> System.out.println("error"));
    socket.connect();


//    JSONObject createGroup = new JSONObject();
//    try {
//      createGroup.put("type", 2);
//      createGroup.put("owner_uin", "281474976981364");
//      createGroup.put("name", "type2_011");
//      createGroup.put("avatar_url", "");
//      createGroup.put("occupants_uins", Collections.singletonList("281474976981364"));
//      createGroup.put("sender_name", "Admin");
//
//      socket.emit("create_group", createGroup).on("create_group", groupResp -> {
//        JSONObject resp = (JSONObject)groupResp[0];
//        System.out.println(resp);
//      });
//    } catch (JSONException e) {
//      e.printStackTrace();
//    }


//type2_010 - 286081340983552

//
//    //thao - 3096224744741499
//    //unknown - 3096224743817508

    socket.on("update_group", joinGroupResp -> {
      JSONObject resp = (JSONObject)joinGroupResp[0];
      System.out.println(resp.toString());
    });

    JSONObject joinGroupMsg = new JSONObject();
      joinGroupMsg.put("group_id", 286081352828800L);
      joinGroupMsg.put("name", "type2_011");
      joinGroupMsg.put("owner_uin", 281474976981364L);
      joinGroupMsg.put("avatar_url", "");
      joinGroupMsg.put("push_all", Arrays.asList(3096224744741499L));

      socket.emit("update_group", joinGroupMsg);
    Thread.sleep(10000);
    System.out.println("done");
  }
}