package com.heartbeat.db;

import com.couchbase.client.java.kv.UpsertOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface Mapper {
  void    map(String id, String key, Handler<AsyncResult<String>> handler);
  void    mapOverride(String id, String key, UpsertOptions options, Handler<AsyncResult<String>> handler);
  void    unmap(String key, Handler<AsyncResult<String>> handler);
  void    getValue(String key, Handler<AsyncResult<String>> handler);

  String  map(String id, String key);
  String  mapOverride(String id, String key, UpsertOptions options);

  String  unmap(String key);
  String  getValue(String key);
}