package com.heartbeat.db.cb;

import com.common.LOG;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.kv.GetResult;
import com.heartbeat.HBServer;
import com.heartbeat.db.Cruder;
import com.heartbeat.db.dao.LeaderBoardDAO;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public class CBLeaderBoard implements Cruder<LeaderBoardDAO> {
  private ReactiveBucket rxIndexBucket;
  private CBLeaderBoard() {
    rxIndexBucket = HBServer.rxIndexBucket;
  }

  private static CBLeaderBoard instance = new CBLeaderBoard();
  public static CBLeaderBoard getInstance() {
    return instance;
  }

  @Override
  public void load(String id, Handler<AsyncResult<LeaderBoardDAO>> handler) {

  }

  @Override
  public void load(String id, String password, Handler<AsyncResult<LeaderBoardDAO>> handler) {

  }

  @Override
  public void sync(String id, LeaderBoardDAO obj, Handler<AsyncResult<String>> handler) {

  }

  @Override
  public void add(String id, LeaderBoardDAO obj, Handler<AsyncResult<String>> handler) {

  }

  @Override
  public void remove(String id, Handler<AsyncResult<String>> handler) {

  }

  @Override
  public LeaderBoardDAO load(String id) {
    try {
      GetResult gr = rxIndexBucket.defaultCollection().get(id).block();
      if (gr != null)
        return gr.contentAs(LeaderBoardDAO.class);
      return LeaderBoardDAO.ofDefault();
    }
    catch (Exception e) {
      return LeaderBoardDAO.ofDefault();
    }
  }

  @Override
  public LeaderBoardDAO load(String id, String password) {
    return null;
  }

  @Override
  public boolean sync(String id, LeaderBoardDAO obj) {
    try {
      rxIndexBucket.defaultCollection().upsert(id, obj).block();
      return true;
    }
    catch (Exception e) {
      LOG.globalException("node","CBLeaderboad:sync",e);
      return false;
    }
  }

  @Override
  public boolean add(String id, LeaderBoardDAO obj) {
    return false;
  }

  @Override
  public boolean remove(String id) {
    return false;
  }
}
