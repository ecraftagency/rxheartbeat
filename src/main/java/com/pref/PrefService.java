package com.pref;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface PrefService {
  boolean haveUpLink(String id);
  void    linkIdentity(String id, String upLink, Handler<AsyncResult<Integer>> handler);
  void    addProfile(String id, long profileId);
  void    linkCnt(String id, Handler<AsyncResult<Integer>> handler);
  void    loadIdentity(String id, Handler<AsyncResult<Identity>> handler);
}