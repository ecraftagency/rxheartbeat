package com.heartbeat.db.dao;

import com.transport.model.NetAward;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetAwardDAO {
  public int lastSync;
  public Map<Integer, NetAward> attractiveTitle;
  public Map<Integer, NetAward> stylishTitle;
  public Map<Integer, NetAward> brandTitle;
  public Map<Integer, NetAward> dedicatedTitle;
  public Map<Integer, NetAward> allTimeTitle;

  public static NetAwardDAO of( Map<Integer, NetAward> attractiveTitle,
                                Map<Integer, NetAward> stylishTitle,
                                Map<Integer, NetAward> brandTitle,
                                Map<Integer, NetAward> dedicatedTitle,
                                Map<Integer, NetAward> allTimeTitle) {
    NetAwardDAO dao = new NetAwardDAO();
    dao.attractiveTitle = attractiveTitle;
    dao.stylishTitle = stylishTitle;
    dao.brandTitle = brandTitle;
    dao.dedicatedTitle = dedicatedTitle;
    dao.allTimeTitle = allTimeTitle;
    dao.lastSync = (int)(System.currentTimeMillis()/1000);
    return dao;
  }

  public static NetAwardDAO ofDefault() {
    NetAwardDAO dao = new NetAwardDAO();
    dao.lastSync = 0;
    dao.attractiveTitle = new ConcurrentHashMap<>();
    dao.stylishTitle = new ConcurrentHashMap<>();
    dao.brandTitle = new ConcurrentHashMap<>();
    dao.dedicatedTitle = new ConcurrentHashMap<>();
    dao.allTimeTitle = new ConcurrentHashMap<>();
    return dao;
  }
}
