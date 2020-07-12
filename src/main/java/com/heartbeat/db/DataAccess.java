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
  //async
  void    load(String id, Handler<AsyncResult<T>> handler);
  void    load(String id, String password, Handler<AsyncResult<T>> handler);
  void    sync(String id, T obj, Handler<AsyncResult<String>> handler);
  void    add(String id, T obj, Handler<AsyncResult<String>> handler);
  void    map(String id, String key, Handler<AsyncResult<String>> handler);
  void    unmap(String key, Handler<AsyncResult<String>> handler);

  //sync
  T       load(String id);
  T       load(String id, String password);
  boolean sync(String id, T obj);
  boolean add(String id, T obj);
  String  map(String id, String key);
  String  unmap(String key);
  long    nextId();
}