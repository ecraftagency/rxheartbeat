package com.heartbeat.db.cb;

import com.common.LOG;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.InsertOptions;
import com.couchbase.client.java.kv.MutationResult;
import com.heartbeat.HBServer;
import com.heartbeat.db.Cruder;
import com.transport.model.NetAward;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import static com.common.Constant.*;
import java.time.Duration;

public class CBNetAward implements Cruder<NetAward> {

  private ReactiveBucket    rxIndexBucket;

  private CBNetAward() {
    rxIndexBucket   = HBServer.rxIndexBucket;
  }

  private static CBNetAward instance = new CBNetAward();
  public static CBNetAward getInstance() {
    return instance;
  }

  @Override
  public void load(String id, Handler<AsyncResult<NetAward>> handler) {
    handler.handle(Future.failedFuture("unimplemented"));
  }

  @Override
  public void load(String id, String password, Handler<AsyncResult<NetAward>> handler) {
    handler.handle(Future.failedFuture("unimplemented"));
  }

  @Override
  public void sync(String id, NetAward obj, Handler<AsyncResult<String>> handler) {
    handler.handle(Future.failedFuture("unimplemented"));
  }

  @Override
  public void add(String id, NetAward obj, Handler<AsyncResult<String>> handler) {
    handler.handle(Future.failedFuture("unimplemented"));
  }

  @Override
  public void remove(String id, Handler<AsyncResult<String>> handler) {
    handler.handle(Future.failedFuture("not implemented"));
  }

  @Override
  public NetAward load(String id) {
    try {
      GetResult result = rxIndexBucket.defaultCollection().get(id).block();
      if (result != null)
        return result.contentAs(NetAward.class);
      else
        return null;
    }
    catch (Exception e) {
      return null;
    }
  }

  @Override
  public NetAward load(String id, String password) {
    //todo implementation
    return null;
  }

  @Override
  public boolean sync(String id, NetAward obj) {
    //todo implementation
    return false;
  }

  @Override
  public boolean add(String id, NetAward obj) {
    try {
      MutationResult result = rxIndexBucket.defaultCollection()
              .insert(id, obj, InsertOptions
                      .insertOptions()
                      .expiry(Duration.ofMinutes(NET_AWARD.EXPIRY)))
              .block();
      return result != null;
    }
    catch (Exception e) {
      LOG.globalException("node", "cbNetAward:add", e);
      return false;
    }
  }

  @Override
  public boolean remove(String id) {
    return false;
  }
}