package com.heartbeat.db;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/*
 * concrete interface for this game context
 * provide async io operations
 * Couchbase impl by default
 * come and impl your mongodb instance :)
 */

public interface DataAccess<T> {
  void    load(String id, Handler<AsyncResult<T>> handler);
  void    load(String id, String password, Handler<AsyncResult<T>> handler);
  void    sync(String id, T obj, Handler<AsyncResult<String>> handler);
  void    add(String id, T obj, Handler<AsyncResult<String>> handler);
  void    map(String id, String key, Handler<AsyncResult<String>> handler);
  String  map(String id, String key);
  void    unmap(String key, Handler<AsyncResult<String>> handler);
  String  unmap(String key);
  long    nextId();
}