package com.heartbeat.db.cb;

import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.UpsertOptions;
import com.heartbeat.HBServer;
import com.heartbeat.db.Mapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import java.time.Duration;

public class CBMapper implements Mapper {
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
  public void mapOverride(String id, String key, int expiry, Handler<AsyncResult<String>> handler) {
    rxIndexBucket.defaultCollection().upsert(key, id, UpsertOptions.upsertOptions().expiry(Duration.ofMinutes(expiry))).subscribe(
            res -> handler.handle(Future.succeededFuture("ok")),
            err -> handler.handle(Future.failedFuture(err.getMessage())));
  }

  @Override
  public void mapOverride(String id, String key, Handler<AsyncResult<String>> handler) {
    rxIndexBucket.defaultCollection().upsert(key, id).subscribe(
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
  public void getValue(String key, Handler<AsyncResult<String>> handler) {
    rxIndexBucket.defaultCollection().get(key).subscribe(
            res -> handler.handle(Future.succeededFuture(res.contentAs(String.class))),
            err -> handler.handle(Future.failedFuture(err.getMessage()))
    );
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
  public String mapOverride(String id, String key, int expiry) {
    try {
      rxIndexBucket.defaultCollection().upsert(key, id,
              UpsertOptions.upsertOptions().expiry(Duration.ofMinutes(expiry))).block();
      return "ok";
    }
    catch (Exception e) {
      return e.getMessage();
    }
  }

  @Override
  public String mapOverride(String id, String key) {
    try {
      rxIndexBucket.defaultCollection().upsert(key, id).block();
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

  @Override
  public String getValue(String key) {
    try {
      GetResult gr = rxIndexBucket.defaultCollection().get(key).block();
      if (gr != null)
        return gr.contentAs(String.class);
      return "";
    }
    catch (Exception e) {
      return "";
    }
  }
}