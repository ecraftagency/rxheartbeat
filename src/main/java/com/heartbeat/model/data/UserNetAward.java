package com.heartbeat.model.data;

import com.common.LOG;
import com.heartbeat.db.cb.AbstractCruder;
import com.heartbeat.db.dao.NetAwardDAO;
import com.transport.model.NetAward;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserNetAward {
  public static final int ATTRACTIVE_TITLE  = 1;
  public static final int STYLISH_TITLE     = 2;
  public static final int BRAND_TITLE       = 3;
  public static final int DEDICATED_TITLE   = 4;
  public static final int ALL_TIME_TITLE    = 5;

  private static Map<Integer, ConcurrentHashMap<Integer, NetAward>> titles;
  private static final String dbKey = "NetAward";

  public  static AbstractCruder<NetAwardDAO> cbNetAward;

  static {
    cbNetAward        = new AbstractCruder<>(NetAwardDAO.class);
    titles            = new ConcurrentHashMap<>();
    titles.put(ATTRACTIVE_TITLE, new ConcurrentHashMap<>());
    titles.put(STYLISH_TITLE, new ConcurrentHashMap<>());
    titles.put(BRAND_TITLE, new ConcurrentHashMap<>());
    titles.put(DEDICATED_TITLE, new ConcurrentHashMap<>());
    titles.put(ALL_TIME_TITLE, new ConcurrentHashMap<>());
  }

  public static void loadNetAwardFromDB() {
    NetAwardDAO dao = cbNetAward.load(dbKey);
    if (dao == null)
      dao = NetAwardDAO.ofDefault();

    if (dao.attractiveTitle != null)
      titles.get(ATTRACTIVE_TITLE).putAll(dao.attractiveTitle);
    if (dao.stylishTitle != null)
      titles.get(STYLISH_TITLE).putAll(dao.stylishTitle);
    if (dao.brandTitle != null)
      titles.get(BRAND_TITLE).putAll(dao.brandTitle);
    if (dao.dedicatedTitle != null)
      titles.get(DEDICATED_TITLE).putAll(dao.dedicatedTitle);
    if (dao.allTimeTitle != null)
      titles.get(ALL_TIME_TITLE).putAll(dao.allTimeTitle);
  }

  public static void syncNetAwardToDB() {
    NetAwardDAO dao = NetAwardDAO.of( titles.get(ATTRACTIVE_TITLE),
                                      titles.get(STYLISH_TITLE),
                                      titles.get(BRAND_TITLE),
                                      titles.get(DEDICATED_TITLE),
                                      titles.get(ALL_TIME_TITLE));
    cbNetAward.sync(dbKey, dao);
  }

  public static boolean addNetAward(int titleId, NetAward netAward) {
    Map<Integer, NetAward> title = titles.get(titleId);
    if (title == null)
      return false;
    title.put(netAward.id, netAward);
    return true;
  }

  public static List<NetAward> getNetAward(int titleId) {
    List<NetAward> res = new ArrayList<>();
    ConcurrentHashMap<Integer, NetAward> title = titles.get(titleId);
    if (title == null)
      return res;

    int curSec = (int)(System.currentTimeMillis()/1000);
    Enumeration<Integer> e = title.keys();
    while (e.hasMoreElements()) {
      Integer userID = e.nextElement();
      try {
        NetAward netAward = title.get(userID);
        if (netAward != null) {
          if (curSec - netAward.addedTime > 180) { //todo change later
            title.remove(userID);
          }
          else {
            res.add(netAward);
          }
        }
      }
      catch (Exception ex) {
        LOG.globalException("node", "getNetAward", ex);
      }
    }
    return res;
  }
//
//  List<Integer> getUserAward() {
//    return new ArrayList<>();
//  }
}