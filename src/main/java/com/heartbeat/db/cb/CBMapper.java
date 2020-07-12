package com.heartbeat.db.cb;

import com.couchbase.client.java.ReactiveBucket;
import com.heartbeat.HBServer;
import com.heartbeat.db.Mapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class CBMapper implements Mapper {
  private static final Logger LOGGER    = LoggerFactory.getLogger(CBMapper.class);
  private static CBMapper instance      = new CBMapper();
  private ReactiveBucket rxIndexBucket;

  private CBMapper() {
    rxIndexBucket   = HBServer.rxIndexBucket;
  }

  public static CBMapper getInstance() {
    return instance;
  }

  @Override
  public void map(String id, String key, Handler<AsyncResult<String>> handler) {
    rxIndexBucket.defaultCollection().insert(key, id).subscribe(
            res -> handler.handle(Future.succeededFuture("ok")),
            err -> handler.handle(Future.failedFuture(err.getMessage())));
  }

  @Override
  public void unmap(String key, Handler<AsyncResult<String>> handler) {
    rxIndexBucket.defaultCollection().remove(key).subscribe(
            res -> handler.handle(Future.succeededFuture("ok")),
            err -> handler.handle(Future.failedFuture(err.getMessage())));
  }

  @Override
  public String map(String id, String key) {
    try {
      rxIndexBucket.defaultCollection().insert(key, id).block();
      return "ok";
    }
    catch (Exception e) {
      return e.getMessage();
    }
  }

  @Override
  public String unmap(String key) {
    try {
      rxIndexBucket.defaultCollection().remove(key).block();
      return "ok";
    }
    catch (Exception e) {
      return e.getMessage();
    }
  }
}