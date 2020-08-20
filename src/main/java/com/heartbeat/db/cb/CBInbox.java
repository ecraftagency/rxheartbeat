package com.heartbeat.db.cb;

import com.common.LOG;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.kv.GetResult;
import com.heartbeat.HBServer;
import com.heartbeat.db.Cruder;
import com.heartbeat.db.dao.PublicMailBoxDAO;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public class CBInbox implements Cruder<PublicMailBoxDAO> {
  private               ReactiveBucket  rxIndexBucket;

  private CBInbox() {
    rxIndexBucket = HBServer.rxIndexBucket;
  }

  private static  CBInbox instance = new CBInbox();
  public static   CBInbox getInstance() {
    return instance;
  }

  @Override
  public void load(String id, Handler<AsyncResult<PublicMailBoxDAO>> handler) {

  }

  @Override
  public void load(String id, String password, Handler<AsyncResult<PublicMailBoxDAO>> handler) {

  }

  @Override
  public void sync(String id, PublicMailBoxDAO obj, Handler<AsyncResult<String>> handler) {

  }

  @Override
  public void add(String id, PublicMailBoxDAO obj, Handler<AsyncResult<String>> handler) {

  }

  @Override
  public void remove(String id, Handler<AsyncResult<String>> handler) {

  }

  @Override
  public PublicMailBoxDAO load(String id) {
    try {
      GetResult gr = rxIndexBucket.defaultCollection().get(id).block();
      if (gr != null)
        return gr.contentAs(PublicMailBoxDAO.class);
      return PublicMailBoxDAO.ofDefault();
    }
    catch (Exception e) {
      return PublicMailBoxDAO.ofDefault();
    }
  }

  @Override
  public PublicMailBoxDAO load(String id, String password) {
    return null;
  }

  @Override
  public boolean sync(String id, PublicMailBoxDAO obj) {
    try {
      rxIndexBucket.defaultCollection().upsert(id, obj).block();
      return true;
    }
    catch (Exception e) {
      LOG.globalException(e);
      return false;
    }
  }

  @Override
  public boolean add(String id, PublicMailBoxDAO obj) {
    return false;
  }

  @Override
  public boolean remove(String id) {
    return false;
  }
}
