package com.heartbeat.db;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@SuppressWarnings("unused")
public interface Mapper {
  void    map(String id, String key, Handler<AsyncResult<String>> handler);
  void    mapOverride(String id, String key, int expiry, Handler<AsyncResult<String>> handler);
  void    mapOverride(String id, String key, Handler<AsyncResult<String>> handler);

  void    unmap(String key, Handler<AsyncResult<String>> handler);
  void    getValue(String key, Handler<AsyncResult<String>> handler);

  String  map(String id, String key);
  String  mapOverride(String id, String key, int expiry);
  String  mapOverride(String id, String key);

  String  unmap(String key);
  String  getValue(String key);
}