package com.heartbeat.db;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/*
 * concrete interface for this game context
 * provide sync/async CRU[D] operations
 * Couchbase impl by default
 * come and impl your Mongodb or Mysql instance :)
 */

public interface Cruder<T> {
  //async
  void    load(String id, Handler<AsyncResult<T>> handler);
  void    load(String id, String password, Handler<AsyncResult<T>> handler);
  void    sync(String id, T obj, Handler<AsyncResult<String>> handler);
  void    add(String id, T obj, Handler<AsyncResult<String>> handler);
  void    remove(String id, Handler<AsyncResult<String>> handler);
  void    sync(String id, T obj, Handler<AsyncResult<String>> handler, long expire);

  //sync
  T       load(String id);
  T       load(String id, String password);
  boolean sync(String id, T obj);
  boolean add(String id, T obj);
  boolean remove(String id);
}