package com.heartbeat.db.impl;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.ReactiveCluster;
import static com.heartbeat.common.Constant.*;

import com.heartbeat.common.GlobalVariable;
import com.heartbeat.db.DataAccess;
import com.heartbeat.model.Session;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.couchbase.client.java.kv.LookupInSpec.get;

/*
 * Simple couchbase sync/async io operations
 * repeat after me java[script]!
 */

public class CBDataAccess implements Runnable, DataAccess<Session> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CBDataAccess.class);

  private ReactiveCluster   rxCluster;
  private ReactiveBucket    rxSessionBucket;
  private ReactiveBucket    rxIndexBucket;

  private CouchbaseClient   idIncrementer;
  private CBDataAccess() {
    rxCluster       = ReactiveCluster.connect(DB.HOST, DB.USER, DB.PWD);
    rxSessionBucket = rxCluster.bucket("sessions");
    rxIndexBucket   = rxCluster.bucket("index");

    try {
      List<URI> hosts = new ArrayList<>();
      String uri      = String.format("http://%s:%s/pools", DB.HOST, DB.PORT);
      hosts.add(new URI(uri));
      idIncrementer = new CouchbaseClient(hosts, "index", DB.PWD);
      GlobalVariable.schThreadPool.scheduleAtFixedRate(this, 0,
            DB.COUCHBASE_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
    }
    catch (Exception e) {
      LOGGER.error("error open bucket");
    }
  }

  private static CBDataAccess instance = new CBDataAccess();
  public static CBDataAccess getInstance() {
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
    rxSessionBucket.defaultCollection().upsert(id, obj).subscribe(res -> {
      handler.handle(Future.succeededFuture("ok"));
    }, err -> handler.handle(Future.succeededFuture(err.getMessage())));
  }

  @Override
  public void add(String id, Session obj, Handler<AsyncResult<String>> handler) {
    rxSessionBucket.defaultCollection().insert(id, obj).subscribe(res -> {
      handler.handle(Future.succeededFuture("ok"));
    }, err -> handler.handle(Future.succeededFuture(err.getMessage())));
  }

  @Override
  public void map(String id, String key, Handler<AsyncResult<String>> handler) {
    rxIndexBucket.defaultCollection().insert(key, id).subscribe(res -> {
      handler.handle(Future.succeededFuture("ok"));
    }, err -> handler.handle(Future.failedFuture(err.getMessage())));
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
  public void unmap(String key, Handler<AsyncResult<String>> handler) {
    rxIndexBucket.defaultCollection().remove(key).subscribe(res -> {
      handler.handle(Future.succeededFuture("ok"));
    }, err -> handler.handle(Future.failedFuture(err.getMessage())));
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
  public long nextId() {
    return idIncrementer.incr(DB.INCR_KEY, 1,100001L);
  }

  @Override
  public void run() {
    try {
        idIncrementer.incr(DB.COUCHBASE_CHECK_KEY, 1, 0L);
    }
    catch(Exception e) {
      LOGGER.error(e.getMessage());
    }
  }
}