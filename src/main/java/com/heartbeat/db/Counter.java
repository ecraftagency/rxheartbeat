package com.heartbeat.db;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface Counter {
  long increase(String key, long defaultValue);
  void increase(String key, long defaultValue, Handler<AsyncResult<Long>> handler);
}