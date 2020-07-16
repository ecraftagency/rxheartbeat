import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.heartbeat.common.Utilities;
import com.transport.ExtMessage;
import com.transport.LoginRequest;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

@SuppressWarnings("unused")
public class Request extends AbstractVerticle {
  private static WebClient client;
  private static String HOST = "localhost";
  private static int    PORT = 8080;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    WebClientOptions options = new WebClientOptions()
            .setUserAgent("Test Client/1.0");
    options.setKeepAlive(false);

    client = WebClient.create(vertx);
  }

  @Override
  public void start() throws Exception {
    super.start();

    String cat = "{\"turns\":0,\"histories\":[{\"type\":1,\"name\":\"abc\",\"date\":1594906761548}]}" + "getItem" + "NPW7S4EFSS";

    client.get("http://68.183.180.71:3000/api/v1/log/add")
            .addQueryParam("type", "getItem")
            .addQueryParam("name", "tylinh01")
            .addQueryParam("data", "{\"turns\":0,\"histories\":[{\"type\":1,\"name\":\"abc\",\"date\":1594906761548}]}")
            .addQueryParam("hash", Utilities.md5Encode(cat)).send(ar -> {
              if (ar.succeeded())
                System.out.println(ar.result().bodyAsString());
    });


//    client.post(PORT, "localhost", "/api/auth")
//            .putHeader("Content-Type", "text/json")
//            .sendJson(loginRequest, ar -> {
//              if (ar.succeeded()) {
//                authRequest(client, ar.result().bodyAsJson(ExtMessage.class).data.profile.jwtToken, heartbeat, "/api/system");
//                //authRequest(client, ar.result().bodyAsJson(ExtMessage.class).data.profile.jwtToken, userGameInfo, "/api/profile");
//                authRequest(client, ar.result().bodyAsJson(ExtMessage.class).data.profile.jwtToken, updateInfo, "/api/profile");
//              }
//              else {
//                System.out.println(ar.cause().getMessage());
//              }
//            });
  }

  private static LoginRequest loginRequest;
  private static JsonObject   heartbeat;
  private static JsonObject   userGameInfo;
  private static JsonObject   updateInfo;

  private static Gson gson;
  static {
    gson = new GsonBuilder().setPrettyPrinting().create();
    loginRequest = new LoginRequest();
    loginRequest.userID         = "100001";
    loginRequest.password       = "100001";
    loginRequest.clientVersion  = "150";
    loginRequest.buildSource    = "VN";

    heartbeat = new JsonObject()
            .put("cmd", "heartbeat");

    userGameInfo = new JsonObject()
            .put("cmd", "userGameInfo");

    updateInfo = new JsonObject()
            .put("cmd", "updateInfo")
            .put("gender", 1)
            .put("avatar", 0)
            .put("displayName", "vunguyen");
  }

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle("Request");
  }

  public static void authRequest(WebClient client, String token, JsonObject body, String endpoint) {
    System.out.println("request " + endpoint + " with token: " + token);
    client.post(8080, HOST, endpoint)
    .putHeader("Content-Type", "text/json")
    .putHeader("Authorization", "Bearer " + token)
    .sendJsonObject(body, ar -> {
      if (ar.succeeded()) {
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(ar.result().bodyAsString());
        System.out.println(gson.toJson(je));
      }
      else {
        System.out.println(ar.cause().getMessage());
      }
    });
  }
}