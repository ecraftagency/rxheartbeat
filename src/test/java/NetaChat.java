import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class NetaChat {

  public static void main(String[] args) throws URISyntaxException, InterruptedException {

    IO.Options opts = new IO.Options();
    opts.transports = new String[]{"websocket"};
    opts.query      = "session=54ca583b474538c5f77bab6fa30f1598972db50d";
    opts.reconnection = false;

    Socket socket = IO.socket("http://dev.conn1.netalo.vn:2082/data", opts);
    socket
    .on(Socket.EVENT_CONNECT, args1 -> System.out.println("connected"))
    .on(Socket.EVENT_ERROR, args12 -> System.out.println("error"));
    socket.connect();

    Thread.sleep(10000);
    System.out.println("done");
  }
}