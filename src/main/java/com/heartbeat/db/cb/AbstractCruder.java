package com.heartbeat.db.cb;

import com.common.LOG;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.UpsertOptions;
import com.heartbeat.HBServer;
import com.heartbeat.db.Cruder;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.time.Duration;

public class AbstractCruder<T> implements Cruder<T> {
  private ReactiveBucket rxBucket;
  private Class<T> type;

  public AbstractCruder(Class<T> type, ReactiveBucket bucket) {
    rxBucket = bucket;
    this.type = type;
  }

  @Override
  public void load(String id, Handler<AsyncResult<T>> handler) {

  }

  @Override
  public void load(String id, String password, Handler<AsyncResult<T>> handler) {

  }

  @Override
  public void sync(String id, T obj, Handler<AsyncResult<String>> handler) {
    try {
      rxBucket.defaultCollection().upsert(id, obj);
    }
    catch (Exception e) {
      LOG.globalException("node",String.format("%s:sync2DB", type.getName()),e);
    }
  }

  @Override
  public void add(String id, T obj, Handler<AsyncResult<String>> handler) {

  }

  @Override
  public void remove(String id, Handler<AsyncResult<String>> handler) {

  }

  @Override
  public void sync(String id, T obj, Handler<AsyncResult<String>> handler, long expireSecond) {
    try {
      rxBucket.defaultCollection().upsert(id, obj, UpsertOptions.upsertOptions().expiry(Duration.ofSeconds(expireSecond))).subscribe(
              res -> System.out.println("ok"),
              err -> System.out.println(err.getMessage())
      );
    }
    catch (Exception e) {
      LOG.globalException("node",String.format("%s:sync2DB", type.getName()),e);
    }
  }

  @Override
  public T load(String id) {
    try {
      GetResult gr = rxBucket.defaultCollection().get(id).block();
      if (gr != null)
        return gr.contentAs(type);
      return null;
    }
    catch (Exception e) {
      LOG.globalException("node",String.format("%s:loadFromDB", type.getName()),e);
      return null;
    }
  }

  @Override
  public T load(String id, String password) {
    return null;
  }

  @Override
  public boolean sync(String id, T obj) {
    try {
      rxBucket.defaultCollection().upsert(id, obj).block();
      return true;
    }
    catch (Exception e) {
      LOG.globalException("node",String.format("%s:sync2DB", type.getName()),e);
      return false;
    }
  }

  @Override
  public boolean add(String id, T obj) {
    return false;
  }

  @Override
  public boolean remove(String id) {
    return false;
  }
}
