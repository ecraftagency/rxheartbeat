package com.heartbeat.model;

import com.heartbeat.common.Constant;
import com.heartbeat.common.GlobalVariable;
import com.statics.GameShowData;
import com.statics.RunShowData;
import com.statics.ShoppingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class SessionPool {
  private static final Logger LOGGER = LoggerFactory.getLogger(SessionPool.class);
  static ConcurrentHashMap<Integer, Session> pool = new ConcurrentHashMap<>();
  private static int maxCCU;
  public static int maxTime;

  public static int getMaxCCU() {
    return maxCCU;
  }

  public static Session getSessionFromPool(int userID)
  {
    return pool.get(userID);
  }

  public static int getCCU() {
    return pool.size();
  }

  static void removeSession(int userID) {
    pool.remove(userID);
  }

  public static void addSession(Session session) {
    if (session != null && pool.put(session.id, session) == null) {
      int ccu = pool.size();
      if (ccu > maxCCU) {
        maxCCU = ccu;
        maxTime = (int) (System.currentTimeMillis() / 1000);
      }
    }
  }

  public static void removeAll() {
    Enumeration<Integer> e = pool.keys();
    while (e.hasMoreElements()) {
      Integer userID = e.nextElement();
      try {
        Session session = pool.get(userID);
        if (session != null)
          session.close();
      }
      catch (Exception ex) {
        LOGGER.error(ex.getMessage());
      }
    }
  }

  /********************************************************************************************************************/
  //CHECK HEARTBEAT, MAY THREAT SAFETY BE WITH YOU ^^!
  public static Runnable checkHeartBeat = new Runnable() {
    @Override
    public void run() {
      try {
        int second = (int)(System.currentTimeMillis()/1000);
        Enumeration<Integer> e = pool.keys();
        while (e.hasMoreElements()) {
          Integer sessionId = e.nextElement();
          Session session = pool.get(sessionId);
          if (session != null) {
            if (second - session.lastHearBeatTime >= Constant.ONLINE_INFO.ONLINE_HEARTBEAT_TIME) {
              pool.remove(sessionId);
              session.close();
            }
          }
        }
        LOGGER.info("Session online " + pool.size());
      }
      catch(Exception ex) {
        LOGGER.error(ex.getMessage());
      }
      GlobalVariable.schThreadPool.schedule(this,
              Constant.ONLINE_INFO.ONLINE_HEARTBEAT_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
    }
  };

  public static Runnable dailyReset = () -> {
    try {
      int second = (int)(System.currentTimeMillis()/1000);
      Enumeration<Integer> e = pool.keys();
      while (e.hasMoreElements()) {
        Integer sessionId = e.nextElement();
        Session session = pool.get(sessionId);
        if (session != null) {
          session.userProfile.newDay();

          session.userFight.usedIdols.clear();
          session.userFight.restoreIdols.clear();
          session.userFight.currentRunShow = RunShowData.of(1);
          session.userFight.currentShopping = ShoppingData.of(1);

          session.userDailyMission.newDay();
          session.userGameInfo.newDay();
          session.userInventory.newDay();
          session.userTravel.newDay();
          session.userProduction.newDay();
          session.userIdol.newDay();
        }
      }
    }
    catch(Exception ex) {
      LOGGER.error(ex.getMessage());
    }
  };

  public static Runnable resetGameShowIdols = () -> {
    try {
      Enumeration<Integer> e = pool.keys();
      while (e.hasMoreElements()) {
        Integer sessionId = e.nextElement();
        Session session = pool.get(sessionId);
        if (session != null) {
          session.userFight.currentGameShow = GameShowData.of(1);
          session.userFight.gameShowUsedIdols.clear();
          session.userFight.gameShowRestoreIdols.clear();
        }
      }
      LOGGER.info("CCU " + pool.size());
    }
    catch(Exception ex) {
      LOGGER.error(ex.getMessage());
    }
  };
}
