package com.heartbeat.db;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface Map {
  void    map(String id, String key, Handler<AsyncResult<String>> handler);
  void    unmap(String key, Handler<AsyncResult<String>> handler);
  String  map(String id, String key);
  String  unmap(String key);
}