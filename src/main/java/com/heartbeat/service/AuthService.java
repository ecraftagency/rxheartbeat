package com.heartbeat.service;

import com.transport.LoginRequest;
import com.transport.model.Profile;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface AuthService {
  void processLogin(LoginRequest request, long curMs, Handler<AsyncResult<Profile>> handler);
}