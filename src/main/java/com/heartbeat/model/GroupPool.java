package com.heartbeat.model;

import com.common.Constant;
import com.common.GlobalVariable;
import com.common.LOG;
import com.heartbeat.db.cb.CBGroup;
import com.heartbeat.model.data.UserGroup;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

// Group set is a subset of Session set
@SuppressWarnings("unused")
public class GroupPool {
  static ConcurrentHashMap<Integer, UserGroup> pool = new ConcurrentHashMap<>();

  public static UserGroup getGroupFromPool(int groupID) {
    return pool.get(groupID);
  }

  public static void removeGroup(int groupID) {
    pool.remove(groupID);
  }

  public static void addGroup(UserGroup group) {
    if (group != null && pool.get(group.id) == null) {
      pool.put(group.id, group);
    }
  }

  public static void removeAll() {
    Enumeration<Integer> e = pool.keys();
    while(e.hasMoreElements()) {
      Integer groupID = e.nextElement();
      try {
        UserGroup group = pool.get(groupID);
        if (group != null) {
          group.close();
        }
      }
      catch (Exception ex) {
        LOG.poolException(ex);
      }
    }
  }

  /********************************************************************************************************************/
  //WORKER
  public static Runnable groupSyncTask = new Runnable() {
    @Override
    public void run() {
      try {
        Enumeration<Integer> e = pool.keys();
        while (e.hasMoreElements()) {
          Integer groupID = e.nextElement();
          UserGroup group = pool.get(groupID);
          if (group != null && group.isChange) {
            CBGroup.getInstance().sync(Integer.toString(group.id), group, ar -> {
              group.isChange = false;
              if (!ar.succeeded())
                LOG.poolException(e);
            });
          }
        }
        //LOGGER.info("Group online " + pool.size());
      }
      catch (Exception e) {
        LOG.poolException(e);
      }
      GlobalVariable.schThreadPool.schedule(this,
              Constant.ONLINE_INFO.SYNC_GROUP_INTERVAL, TimeUnit.MILLISECONDS);
    }
  };
}