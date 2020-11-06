package com.heartbeat.db.cb;

import com.common.LOG;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.kv.MutationResult;
import com.heartbeat.HBServer;
import com.common.Constant;
import com.common.GlobalVariable;
import com.heartbeat.db.Cruder;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.data.UserGroup;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@SuppressWarnings("unused")
public class CBGroup implements Cruder<UserGroup> {
  private ReactiveBucket      rxPersistBucket;
  private static CBGroup      instance = new CBGroup();
  private static final String keyPrefix = "group";

  private CBGroup() {
    rxPersistBucket = HBServer.rxPersistBucket;
  }

  public static CBGroup getInstance() {
    return instance;
  }

  @Override
  public void load(String id, Handler<AsyncResult<UserGroup>> handler) {
    StringBuilder builder = GlobalVariable.stringBuilder.get();
    builder.append(keyPrefix).append("_").append(id);

    rxPersistBucket.defaultCollection().get(builder.toString()).subscribe(res -> {
      try {
        UserGroup group = res.contentAs(UserGroup.class);
        group.isChange  = false;
        handler.handle(Future.succeededFuture(group));
      }
      catch (Exception e) {
        handler.handle(Future.failedFuture(e.getMessage()));
      }
    }, err -> handler.handle(Future.failedFuture(err)));
  }

  @Override
  public void load(String id, String password, Handler<AsyncResult<UserGroup>> handler) {
    handler.handle(Future.failedFuture("not_support"));
  }

  @Override
  public void sync(String id, UserGroup obj, Handler<AsyncResult<String>> handler) {
    StringBuilder builder = GlobalVariable.stringBuilder.get();
    builder.append(keyPrefix).append("_").append(id);

    rxPersistBucket.defaultCollection().upsert(builder.toString(), obj).subscribe(
            res -> handler.handle(Future.succeededFuture("ok")),
            err -> handler.handle(Future.succeededFuture(err.getMessage())));
  }

  @Override
  public void add(String id, UserGroup obj, Handler<AsyncResult<String>> handler) {
    CBCounter.getInstance().increase(Constant.DB.GID_INCR_KEY, Constant.DB.GID_INIT, idRes -> {
      if (idRes.succeeded()) {
        obj.id = idRes.result().intValue();
        StringBuilder builder = GlobalVariable.stringBuilder.get();
        builder.append(keyPrefix).append("_").append(obj.id);

        CBMapper.getInstance().map(Integer.toString(obj.id),Integer.toString(obj.owner), mapRes -> {
          if (mapRes.succeeded()) {
            rxPersistBucket.defaultCollection().insert(builder.toString(), obj).subscribe(
                res -> {
                  //ok, then add group to pool
                  GroupPool.addGroup(obj);
                  handler.handle(Future.succeededFuture(Integer.toString(obj.id)));
                },
                err -> {
                  //if add group fail, then rollback
                  CBMapper.getInstance().unmap(Integer.toString(obj.owner));
                  handler.handle(Future.failedFuture(err.getMessage()));
                  LOG.globalException("node", "CBGroup", "id:%s, gid:%d", id, obj.id);
                }
            );
          }
          else {
            handler.handle(Future.failedFuture(mapRes.cause().getMessage()));
          }
        });
      }
      else {
        handler.handle(Future.failedFuture(idRes.cause().getMessage()));
      }
    });
  }

  @Override
  public void remove(String id, Handler<AsyncResult<String>> handler) {
    UserGroup group = GroupPool.getGroupFromPool(Integer.parseInt(id));
    if (group != null) {
      CBMapper.getInstance().unmap(Integer.toString(group.owner), unmapRes -> {
        if (unmapRes.succeeded()) {
          StringBuilder builder = GlobalVariable.stringBuilder.get();
          builder.append(keyPrefix).append("_").append(id);
          rxPersistBucket.defaultCollection().remove(builder.toString()).subscribe(
                  res -> {
                    GroupPool.removeGroup(group.id);
                    handler.handle(Future.succeededFuture("ok"));
                  },
                  err -> {
                    handler.handle(Future.failedFuture(err.getMessage()));
                    LOG.globalException("node", "createGroup", "red alert: already unmap sid_gid but fail to delete group from db, gid: " + id + " sid: " + group.owner);
                  }
          );
        }
        else {
          LOG.globalException("node", "removeGroup", "red alert: sid_gid fail to unmap but have group instance online, gid: " + id + " sid: " + group.owner);
          handler.handle(Future.failedFuture(unmapRes.cause().getMessage()));
        }
      });
    }
    else
      handler.handle(Future.failedFuture("group_not_found"));
  }

  @Override
  public void sync(String id, UserGroup obj, Handler<AsyncResult<String>> handler, long expire) {

  }

  @Override
  public UserGroup load(String id) {
    //todo implementation
    return null;
  }

  @Override
  public UserGroup load(String id, String password) {
    //todo implementation
    return null;
  }

  @Override
  public boolean sync(String id, UserGroup obj) {
    //todo implementation
    return false;
  }

  @Override
  public boolean add(String id, UserGroup obj) {
    //todo implementation
    return false;
  }

  @Override
  public boolean remove(String id) {
    try {
      StringBuilder builder = GlobalVariable.stringBuilder.get();
      builder.append(keyPrefix).append("_").append(id);
      MutationResult a = rxPersistBucket.defaultCollection().remove(builder.toString()).block();
      return a != null;
    }
    catch (Exception e) {
      return false;
    }
  }
}
