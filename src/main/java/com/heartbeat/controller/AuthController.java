package com.heartbeat.controller;

import com.common.Constant;
import com.common.LOG;
import com.heartbeat.service.AuthService;
import com.heartbeat.service.impl.SessionLoginService;
import com.transport.ExtMessage;
import com.transport.LoginRequest;
import com.transport.model.Profile;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class AuthController implements Handler<RoutingContext> {
  public static final int TOKEN_EXPIRE_TIME       = 24*60; //minutes
  private AuthService authService;

  private JWTAuth jwt;
  public AuthController(Vertx vertx) {
    authService = new SessionLoginService();
    jwt = JWTAuth.create(vertx, new JsonObject()
            .put("keyStore", new JsonObject()
                    .put("type", "jceks")
                    .put("path", "keystore.jceks")
                    .put("password", "secret")));
  }

  @Override
  public void handle(RoutingContext ctx) {
    LoginRequest lr = Json.decodeValue(ctx.getBody(), LoginRequest.class);
    ExtMessage resp = ExtMessage.system();
    resp.cmd = "login";

    authService.processLogin(lr, System.currentTimeMillis(), ar -> {
      if (ar.succeeded()) {
        Profile profile = ar.result();
        profile.jwtToken = jwt.generateToken(
            new JsonObject().put("username", profile.strUserId).put("issueTime", profile.lastLogin),
            new JWTOptions().setIssuer("Vert.x").setExpiresInMinutes(TOKEN_EXPIRE_TIME));
        resp.data.profile     = profile;
        resp.unlockFunction   = unlockFunction();
        resp.msg = "ok";
      }
      else {
        resp.msg = ar.cause().getMessage();
        LOG.authException(ar.cause());
      }
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
    });
  }

  public JWTAuth getJWTProvider() {
    return jwt;
  }

  private static HashMap<String, Integer> unlockFunction() {
    HashMap<String, Integer> unlockMap = new HashMap<>();
    unlockMap.put("TIME_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.TIME_UNLOCK_LEVEL);
    unlockMap.put("GAME_SHOW_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.GAME_SHOW_UNLOCK_LEVEL);
    unlockMap.put("SHOP_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.SHOP_UNLOCK_LEVEL);
    unlockMap.put("TRAVEL_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.TRAVEL_UNLOCK_LEVEL);
    unlockMap.put("GROUP_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.GROUP_UNLOCK_LEVEL);
    unlockMap.put("FRIEND_QR_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.FRIEND_QR_UNLOCK_LEVEL);
    unlockMap.put("SHOPPING_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.SHOPPING_UNLOCK_LEVEL);
    unlockMap.put("SKIP_FIGHT_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.SKIP_FIGHT_UNLOCK_LEVEL);
    unlockMap.put("FAST_SHOPPING_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.FAST_SHOPPING_UNLOCK_LEVEL);
    unlockMap.put("RUN_SHOW_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.RUN_SHOW_UNLOCK_LEVEL);
    unlockMap.put("FAST_RUN_SHOW_UNLOCK_LEVEL", Constant.UNLOCK_FUNCTION.FAST_RUN_SHOW_UNLOCK_LEVEL);

    return unlockMap;
  }
}