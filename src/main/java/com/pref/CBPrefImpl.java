package com.pref;

import com.common.LOG;
import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.kv.LookupInOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static com.couchbase.client.java.kv.LookupInSpec.get;
import static com.couchbase.client.java.kv.MutateInSpec.arrayAddUnique;

/*so much painful for sub document operations T____T*/
public class CBPrefImpl implements PrefService {
  Map<Integer, ReactiveBucket> prefBuckets;

  public CBPrefImpl(Map<Integer, ReactiveBucket> prefBuckets) {
    this.prefBuckets = prefBuckets;
  }

  @Override
  public boolean haveUpLink(String id) {
    return false;
  }

  @Override
  public void linkIdentity(String id, String upLink, Handler<AsyncResult<Integer>> handler) {

  }

  @Override
  public void addProfile(String id, long profileId) {
    ReactiveCollection rb = prefBuckets.get(hash(id)).defaultCollection();
    if (rb == null)
      return;
    rb.exists(id).subscribe(
      res -> {
        try {
          if (res.exists())
            rb.mutateIn(id, Collections.singletonList(arrayAddUnique("profiles", profileId))).subscribe(
              r -> {},
              e -> LOG.globalException("Pref", "addProfile:arrayAddUnique", e.getCause()));
          else {
            Identity identity = Identity.ofDefault(id);
            identity.profiles.add(profileId);
            rb.insert(id, identity).subscribe(
              r -> {},
              e -> LOG.globalException("Pref", "addProfile:insert", e.getCause()));
          }
        }
        catch (Exception e) {
          LOG.globalException("Pref", "addProfile", e);
        }
      }
    );
  }

  @Override
  public void linkCnt(String id, Handler<AsyncResult<Integer>> handler) {
    ReactiveCollection rb = prefBuckets.get(hash(id)).defaultCollection();
    if (rb == null) {
      handler.handle(Future.succeededFuture(0));
      return;
    }
    rb.lookupIn(id, Arrays.asList(get("claimLinkReward"), get("links")), LookupInOptions.lookupInOptions()).subscribe(
      r -> {
        try {
          handler.handle(Future.succeededFuture(r.contentAsArray(0).size()));
        }
        catch (Exception e) {
          LOG.globalException("Pref", "linkCnt", e);
        }
      },
      e -> handler.handle(Future.succeededFuture(0))
    );
  }

  @Override
  public void loadIdentity(String id, Handler<AsyncResult<Identity>> handler) {
    ReactiveCollection rb = prefBuckets.get(hash(id)).defaultCollection();
    if (rb == null) {
      handler.handle(Future.succeededFuture(Identity.ofDefault("")));
      LOG.globalException("Pref", "loadIdentity", String.format("identity not found, phoenixId:%s", id));
      return;
    }

    rb.get(id).subscribe(
      nxt -> {
        try {
          Future.succeededFuture(nxt.contentAs(Identity.class));
        }
        catch (Exception e) {
          Future.succeededFuture(Identity.ofDefault(""));
          LOG.globalException("Pref", "loadIdentity", e);
        }
      },
      err -> {
        Future.succeededFuture(Identity.ofDefault(""));
        LOG.globalException("Pref", "loadIdentity", String.format("identity not found, phoenixId:%s", id));
      }
    );
  }

  private int hash(String id) {
    return ((id.hashCode() & 0x7fffffff) % 8) + 1;
  }
}