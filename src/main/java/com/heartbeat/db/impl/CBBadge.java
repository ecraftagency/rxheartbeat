package com.heartbeat.db.impl;

import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.InsertOptions;
import com.couchbase.client.java.kv.MutationResult;
import com.heartbeat.HBServer;
import com.heartbeat.db.DataAccess;
import com.transport.model.Title;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class CBBadge implements DataAccess<Title> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CBBadge.class);
  private static final int    EXPIRY = 2; //minutes

  private ReactiveBucket    rxIndexBucket;

  private CBBadge() {
    rxIndexBucket   = HBServer.rxIndexBucket;
  }

  private static CBBadge instance = new CBBadge();
  public static CBBadge getInstance() {
    return instance;
  }

  @Override
  public void load(String id, Handler<AsyncResult<Title>> handler) {
    handler.handle(Future.failedFuture("unimplemented"));
  }

  @Override
  public void load(String id, String password, Handler<AsyncResult<Title>> handler) {
    handler.handle(Future.failedFuture("unimplemented"));
  }

  @Override
  public void sync(String id, Title obj, Handler<AsyncResult<String>> handler) {
    handler.handle(Future.failedFuture("unimplemented"));
  }

  @Override
  public void add(String id, Title obj, Handler<AsyncResult<String>> handler) {
    handler.handle(Future.failedFuture("unimplemented"));
  }

  @Override
  public void map(String id, String key, Handler<AsyncResult<String>> handler) {
    handler.handle(Future.failedFuture("unimplemented"));
  }

  @Override
  public void unmap(String key, Handler<AsyncResult<String>> handler) {
    handler.handle(Future.failedFuture("unimplemented"));
  }

  @Override
  public String map(String id, String key) {
    //todo implementation
    return null;
  }

  @Override
  public String unmap(String key) {
    //todo implementation
    return null;
  }

  @Override
  public Title load(String id) {
    try {
      GetResult result = rxIndexBucket.defaultCollection().get(id).block();
      if (result != null)
        return result.contentAs(Title.class);
      else
        return null;
    }
    catch (Exception e) {
      return null;
    }
  }

  @Override
  public Title load(String id, String password) {
    //todo implementation
    return null;
  }

  @Override
  public boolean sync(String id, Title obj) {
    //todo implementation
    return false;
  }

  @Override
  public boolean add(String id, Title obj) {
    try {
      MutationResult result = rxIndexBucket.defaultCollection()
              .insert(id, obj, InsertOptions
                      .insertOptions()
                      .expiry(Duration.ofMinutes(EXPIRY)))
              .block();
      return result != null;
    }
    catch (Exception e) {
      LOGGER.error(e.getMessage());
      return false;
    }
  }

  @Override
  public long nextId() {
    //todo implementation
    return 0;
  }
}