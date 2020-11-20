package com.pref;

import com.common.Constant;
import com.common.LOG;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.ReactiveCollection;
import com.transport.Identity;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import java.util.Map;

/*so much painful for sub document operations T____T*/
public class CBPrefImpl implements PrefService {
  Map<Integer, ReactiveBucket> prefBuckets;

  public CBPrefImpl(Map<Integer, ReactiveBucket> prefBuckets) {
    this.prefBuckets = prefBuckets;
  }

  @Override
  public void addProfile(String id, long profileId, Handler<AsyncResult<Identity>> handler) {
    loadIdentity(id, ar -> {
      Identity identity = ar.succeeded() ? ar.result() : Identity.ofDefault(id);
      identity.profiles.add(profileId);
      handler.handle(Future.succeededFuture(identity));
      syncIdentity(id, identity);
    });
  }

  //todo UGLY!
  @Override
  public void linkIdentity(String id, String upLink, Handler<AsyncResult<String>> handler) {
    if (id == null || id.equals("") || upLink == null || upLink.equals("") || upLink.equals(id)) {
      handler.handle(Future.failedFuture("invalid_identity"));
      return;
    }
    loadIdentity(id, ar -> {
      if (ar.succeeded()) {
        Identity identity = ar.result();
        if (identity.upLink != null && !identity.upLink.equals("")) {
          handler.handle(Future.failedFuture("already_link"));
          return;
        }
        loadIdentity(upLink, uar -> {
          if (uar.succeeded()) { //todo should try catch?
            Identity upLinkIdentity = uar.result();
            if (identity.id.equals(upLinkIdentity.upLink)) {
              handler.handle(Future.failedFuture("circle_link"));
              return;
            }
            if (upLinkIdentity.links.size() >= Constant.REF_INFO.MAX_LINK) { //todo hard code
              handler.handle(Future.failedFuture("up_link_full"));
              return;
            }
            if (upLinkIdentity.links.contains(identity.id)) {
              handler.handle(Future.failedFuture("already_linked"));
              return;
            }
            upLinkIdentity.links.add(identity.id);
            identity.upLink = upLinkIdentity.id;
            handler.handle(Future.succeededFuture("ok"));
            syncIdentity(id, identity);
            syncIdentity(upLink, upLinkIdentity);
          }
          else
            handler.handle(Future.failedFuture("up_link_not_exist"));
        });
      }
      else
        handler.handle(Future.failedFuture(ar.cause().getMessage()));
    });
  }

  @Override
  public void claimLinkReward(String id, Handler<AsyncResult<String>> handler) {
    loadIdentity(id, ar -> {
      if (ar.succeeded()) {
        Identity identity = ar.result();
        if (identity.links.size() < Constant.REF_INFO.MAX_LINK) {
          handler.handle(Future.failedFuture("insufficient_claim"));
          return;
        }
        if (identity.claimLinkReward){
          handler.handle(Future.failedFuture("already_claimed"));
          return;
        }
        identity.claimLinkReward = true;
        handler.handle(Future.failedFuture("ok"));
        syncIdentity(id, identity);
      }
    });
  }

  @Override
  public void loadIdentity(String id, Handler<AsyncResult<Identity>> handler) {
    ReactiveCollection rb = prefBuckets.get(hash(id)).defaultCollection();
    if (rb == null) {
      handler.handle(Future.failedFuture("bucket_not_found"));
      LOG.globalException("Pref", "loadIdentity", String.format("bucket not found, phoenixId:%s", id));
      return;
    }

    rb.get(id).subscribe(
      nxt -> {
        try {
          handler.handle(Future.succeededFuture(nxt.contentAs(Identity.class)));
        }
        catch (Exception e) {
          Future.failedFuture("identity_deserialize_fail");
          LOG.globalException("Pref", "loadIdentity", e);
        }
      },
      err -> {
        handler.handle(Future.failedFuture("identity_not_found"));
        LOG.globalException("Pref", "loadIdentity", String.format("identity not found, phoenixId:%s", id));
      }
    );
  }

  @Override
  public void syncIdentity(String id, Identity identity) {
    ReactiveCollection rb = prefBuckets.get(hash(id)).defaultCollection();
    if (rb == null) {
      LOG.globalException("Pref", "syncIdentity", String.format("bucket not found:%s", id));
      return;
    }
    rb.upsert(id, identity).subscribe(res ->{}, err -> {});
  }

  private int hash(String id) { //todo hard code
    return ((id.hashCode() & 0x7fffffff) % Constant.REF_INFO.HASH_CNT) + 1;
  }
}