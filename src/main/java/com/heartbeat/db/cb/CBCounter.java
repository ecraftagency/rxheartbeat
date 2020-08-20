package com.heartbeat.db.cb;

import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.kv.CounterResult;
import com.couchbase.client.java.kv.IncrementOptions;
import com.heartbeat.HBServer;
import com.heartbeat.db.Counter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

//depend upon abstraction not implementation
public class CBCounter implements Counter {
  private static CBCounter instance   = new CBCounter();

  private ReactiveBucket rxIndexBucket;
  public static CBCounter getInstance() {
    return instance;
  }

  private CBCounter() {
    rxIndexBucket   = HBServer.rxIndexBucket;
  }

  @Override
  public long increase(String key, long defaultValue) {
    try {
      CounterResult res = rxIndexBucket.defaultCollection()
              .binary()
              .increment(key, IncrementOptions.incrementOptions().initial(defaultValue))
              .block();
      if (res != null)
        return res.content();
      return -1;
    }
    catch (Exception e) {
      return -1;
    }
  }

  @Override
  public void increase(String key, long defaultValue, Handler<AsyncResult<Long>> handler) {
    rxIndexBucket.defaultCollection().binary().increment(key, IncrementOptions.incrementOptions().initial(defaultValue)).subscribe(
            res -> handler.handle(Future.succeededFuture(res.content())),
            err -> handler.handle(Future.failedFuture(err.getMessage()))
    );
  }
}