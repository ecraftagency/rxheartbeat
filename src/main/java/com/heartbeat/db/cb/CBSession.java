package com.heartbeat.db.cb;

import com.couchbase.client.java.ReactiveBucket;

import com.heartbeat.HBServer;
import com.heartbeat.db.Cruder;
import com.heartbeat.model.Session;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.FailedFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import static com.couchbase.client.java.kv.LookupInSpec.get;

/*
 * Simple couchbase sync/async io operations
 * repeat after me java[script]!
 */

@SuppressWarnings("unused")
public class CBSession implements Cruder<Session> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CBSession.class);
  private ReactiveBucket    rxSessionBucket;
  private CBSession() {
    rxSessionBucket = HBServer.rxSessionBucket;
  }

  private static CBSession instance = new CBSession();
  public static CBSession getInstance() {
    return instance;
  }

  @Override
  public void load(String id, Handler<AsyncResult<Session>> handler) {
    rxSessionBucket.defaultCollection().get(id).subscribe(res -> {
      Session session = res.contentAs(Session.class);
      handler.handle(Future.succeededFuture(session));
    }, err -> handler.handle(Future.failedFuture(err.getMessage())));
  }

  @Override
  public void load(String id, String password, Handler<AsyncResult<Session>> handler) {
    rxSessionBucket.defaultCollection().lookupIn(id,
      Collections.singletonList(get("userProfile.password"))).subscribe(res -> {
        String pwd = res.contentAs(0, String.class);
        if (password.equals(pwd)) {
          load(id, ar -> {
            if (ar.succeeded())
              handler.handle(Future.succeededFuture(ar.result()));
            else
              handler.handle(Future.failedFuture(ar.cause()));
          });
        }
        else {
          handler.handle(Future.failedFuture("wrong_pwd"));
        }
    });
  }

  @Override
  public void sync(String id, Session obj, Handler<AsyncResult<String>> handler) {
    rxSessionBucket.defaultCollection().upsert(id, obj).subscribe(
            res -> handler.handle(Future.succeededFuture("ok")),
            err -> handler.handle(Future.succeededFuture(err.getMessage())));
  }

  @Override
  public void add(String id, Session obj, Handler<AsyncResult<String>> handler) {
    rxSessionBucket.defaultCollection().insert(id, obj).subscribe(
            res -> handler.handle(Future.succeededFuture("ok")),
            err -> handler.handle(Future.succeededFuture(err.getMessage())));
  }

  @Override
  public void remove(String id, Handler<AsyncResult<String>> handler) {
    handler.handle(Future.failedFuture("not implemented"));
  }

  @Override
  public Session load(String id) {
    //todo implementation
    return null;
  }

  @Override
  public Session load(String id, String password) {
    //todo implementation
    return null;
  }

  @Override
  public boolean sync(String id, Session obj) {
    //todo implementation
    return false;
  }

  @Override
  public boolean add(String id, Session obj) {
    //todo implementation
    return false;
  }

  @Override
  public boolean remove(String id) {
    return false;
  }
}