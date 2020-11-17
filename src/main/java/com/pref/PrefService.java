package com.pref;

import com.transport.Identity;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface PrefService {
  void    addProfile(String id, long profileId, Handler<AsyncResult<Identity>> handler);
  void    linkIdentity(String id, String upLink, Handler<AsyncResult<String>> handler);
  void    claimLinkReward(String id, Handler<AsyncResult<String>> handler);
  void    loadIdentity(String id, Handler<AsyncResult<Identity>> handler);
  void    syncIdentity(String id, Identity identity);
}