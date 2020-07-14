package com.heartbeat.db;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface Mapper {
  void    map(String id, String key, Handler<AsyncResult<String>> handler);
  void    unmap(String key, Handler<AsyncResult<String>> handler);
  void    getValue(String key, Handler<AsyncResult<String>> handler);

  String  map(String id, String key);
  String  unmap(String key);
  String  getValue(String key);
}