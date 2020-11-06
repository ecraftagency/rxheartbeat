package com.heartbeat;

import com.common.Constant;
import com.common.Utilities;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class Passport100D {
  public static WebClient webClient;
  public static void verify(String jwtToken, Handler<AsyncResult<Player>> handler) {
    String req = Constant.PASSPORT.ENV.absRequest(jwtToken);
    webClient.requestAbs(HttpMethod.GET, req).send(ar -> {
      if (ar.succeeded()) {
        try {
          JsonObject payload = ar.result().bodyAsJsonObject();
          int success = payload.getInteger("success");
          if (success == 1) {
            String player_id = payload.getJsonObject("data").getString("player_id");
            String username = payload.getJsonObject("data").getString("username");
            handler.handle(Future.succeededFuture(Player.of(player_id, username)));
          }
          else {
            handler.handle(Future.failedFuture("authorization_fail"));
          }
        }
        catch (Exception e) {
          handler.handle(Future.failedFuture("authorization_fail"));
        }
      }
      else {
        handler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  public static class Env {
    public String               host;
    public String               secret;
    public String               paramFormat;
    private static final String path = "/player/verify_v2";

    public static Env of(String host, String secret, String paramFormat) {
      Env e         = new Env();
      e.host        = host;
      e.secret      = secret;
      e.paramFormat = paramFormat;
      return e;
    }

    public String absRequest(String jwtToken) {
      try {
        int timeStamp = (int)(System.currentTimeMillis()/1000);
        String sign = Utilities.md5Encode(Utilities.md5Encode(jwtToken + timeStamp) + secret);
        String param = String.format(paramFormat, jwtToken, timeStamp, sign);
        return host + path + param;
      }
      catch (Exception e) {
        return "";
      }
    }
  }

  public static class Player {
    public String player_id;
    public String username;
    public static Player of(String player_id, String username) {
      Player p = new Player();
      p.player_id   = player_id;
      p.username    = username;
      return p;
    }
  }

//  public static boolean PROD = true;
//  public static Env ENV;
//
//  static {
//    Env dev   = Env.of("https://dev-sdkapi.phoeniz.com/v1", "JKu8xxJR7edfMqUufi1OH2DXxR7qyf6g", "?authorization=%s&timestamp=%d&sign=%s");
//    Env prod  = Env.of("https://sdkapi.phoeniz.com/v1", "JKu8xxJR7edfMqUufi1OH2DXxR7qyf6g", "?authorization=%s&timestamp=%d&sign=%s");
//
//    ENV       = PROD ? prod : dev;
//  }
}
