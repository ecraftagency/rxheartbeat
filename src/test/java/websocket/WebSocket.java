package websocket;

import com.common.Utilities;
import com.transport.WSMessage;
import com.transport.ws.Verify;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.json.Json;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class WebSocket extends AbstractVerticle {
  static {
    disableSslVerification();
  }

  private static void disableSslVerification() {
    try {
      TrustManager[] trustAllCerts = new TrustManager[] {
              new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
              }
      };

      // Install the all-trusting trust manager
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      // Create all-trusting host name verifier
      HostnameVerifier allHostsValid = (hostname, session) -> true;

      // Install the all-trusting host verifier
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    final Properties props = System.getProperties();
    props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
    props.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", Boolean.TRUE.toString());

//    Vertx vertx = Vertx.vertx();
//    vertx.deployVerticle(new Server(), sr -> {
//      if (sr.succeeded())
//        vertx.deployVerticle(new Client(), cr -> {
//          if (cr.succeeded())
//            System.out.println("Client Verticle deployed");
//
//        });
//        System.out.println("Server Verticle deployed");
//    });

    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Client(), sr -> {
      if (sr.succeeded())
      System.out.println("Client Verticle deployed");
    });
  }

  private static class Server extends AbstractVerticle {
    @Override
    public void start() throws Exception {
      startServer(vertx);
      super.start();
    }

    private void startServer(Vertx vertx) {
      HttpServer server = vertx.createHttpServer();

      server.websocketHandler((ctx) -> {
        ctx.writeTextMessage("ping");

        ctx.textMessageHandler((msg) -> {
          System.out.println("Server " + msg);

          if (ThreadLocalRandom.current().nextInt(100) == 0) {
            ctx.close();
          }
          else {
            ctx.writeTextMessage("ping");
          }
        });
      }).listen(8080);
    }
  }

  private static class Client extends AbstractVerticle {
    @Override
    public void start() throws Exception {
      startClient(vertx);
      super.start();
    }

    private void startClient(Vertx vertx) {
      HttpClient client = vertx.createHttpClient(new HttpClientOptions());
      WebSocketConnectOptions ops = new WebSocketConnectOptions();
      ops.setHost("localhost").setPort(9002).setURI("/").addHeader("sessionId", "sessionId");
      client.websocket(ops, (ctx) -> {
//        WSMessage msg = WSMessage.of("verify");
//        msg.verify = new Verify();
//        msg.verify.sessionId = 2000000;
//        ctx.writeTextMessage(Json.encode(msg));

        WSMessage wsMsg2 = WSMessage.of("echo");
        ctx.writeTextMessage(Json.encode(wsMsg2));

        ctx.textMessageHandler((string) -> {
          WSMessage wsMsg = Utilities.gson.fromJson(string, WSMessage.class);
          if (wsMsg.cmd.equals("echo")) {
            System.out.println(wsMsg.echo.msg);
          }
        }).exceptionHandler((e) -> {
          System.out.println("Closed, restarting in 10 seconds");
          restart(client, 5);
        }).closeHandler((e) -> {
          System.out.println("Closed, restarting in 10 seconds");
          restart(client, 10);
        });
      });
    }

    private void restart(HttpClient client, int delay) {
      client.close();
      vertx.setTimer(TimeUnit.SECONDS.toMillis(delay), (__) -> startClient(vertx));
    }
  }
}
