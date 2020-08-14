package com.heartbeat.controller;

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

public class AuthController implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
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
        resp.data.profile = profile;
        resp.msg = "ok";
      }
      else {
        resp.msg = ar.cause().getMessage();
        LOGGER.error(ar.cause().getMessage());
      }
      ctx.response().putHeader("Content-Type", "text/json").end(Json.encode(resp));
    });
  }

  public JWTAuth getJWTProvider() {
    return jwt;
  }
}