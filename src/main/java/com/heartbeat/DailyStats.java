package com.heartbeat;

import com.common.GlobalVariable;
import com.common.LOG;
import com.common.Utilities;
import com.heartbeat.db.cb.AbstractCruder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/*Simple as possible daily not hourly*/
@SuppressWarnings("unused")
public class DailyStats {
  private static DailyStats                   inst              = new DailyStats();
  private static String                       keyPrefix;
  private static long                         startTime;
  private static long                         startOfToday;
  private static AbstractCruder<DailyStats>   dbAccess;
  public static final String                  DATE_FORMAT       = "yyyyMMdd";

  private static final long                   DAY_MILLISECOND   = 24L * 60 * 60 * 1000;
  private static final long                   DB_SYNC_INTERVAL  = 60 * 1000;
  private static final long                   STATS_EXPIRE_SEC  = 86400*90;

  public ConcurrentHashMap<Integer, Integer>  dailyUseItem;
  public ConcurrentHashMap<Integer, Integer>  dailyGainItem;
  public ConcurrentHashMap<Integer, Integer>  dailyBacklogItem;

  public static DailyStats inst() {
    return inst;
  }

  public static Runnable updateTask = new Runnable() {
    @Override
    public void run() {
      long nextUpdate = 0;
      try {
        nextUpdate = inst.update(System.currentTimeMillis());
      }
      catch(Exception ex) {
        LOG.globalException("Node", "DailyStats:update", ex);
      }
      GlobalVariable.schThreadPool.schedule(this, nextUpdate, TimeUnit.MILLISECONDS);
    }
  };

  private DailyStats() {
    dailyUseItem      = new ConcurrentHashMap<>();
    dailyGainItem     = new ConcurrentHashMap<>();
    dailyBacklogItem  = new ConcurrentHashMap<>();
    startTime         = System.currentTimeMillis();
    startOfToday      = 1000L * Utilities.startOfDay(startTime);
    keyPrefix         = "daily_stats";
    dbAccess          = new AbstractCruder<>(DailyStats.class, HBServer.rxStatsBucket);

    IntStream.range(123, 140).forEach(i -> {
      dailyGainItem.putIfAbsent(i, 0);
      dailyUseItem.putIfAbsent(i, 0);
      dailyBacklogItem.putIfAbsent(i, 0);
    });
  }

  public void loadStatsFromDB(long curMs) {
    String key = GlobalVariable.stringBuilder.get().append(keyPrefix).append('_').append(Utilities.formatTime(curMs, DATE_FORMAT)).toString();
    DailyStats saved = dbAccess.load(key);
    if (saved != null) {
      if (saved.dailyUseItem != null)
        dailyUseItem.putAll(saved.dailyUseItem);
      if (saved.dailyGainItem != null)
        dailyGainItem.putAll(saved.dailyGainItem);
      if (saved.dailyBacklogItem != null)
        dailyBacklogItem.putAll(saved.dailyBacklogItem);
    }
  }

  public void saveStatsToDB(long curMs) {
    String key = GlobalVariable.stringBuilder.get().append(keyPrefix).append('_').append(Utilities.formatTime(curMs, DATE_FORMAT)).toString();
    dbAccess.sync(key, this, null, STATS_EXPIRE_SEC); //todo asynchronous
  }

  public long update(long curMs) {
    saveStatsToDB(curMs);
    int dayDiff = Utilities.dayDiff(startTime, curMs);
    boolean isNewDay = dayDiff != 0;

    if (isNewDay)
      newDay();

    long startOfTomorrow = 1000L * Utilities.startOfDay(curMs + DAY_MILLISECOND);
    return Math.min(startOfTomorrow - curMs, DB_SYNC_INTERVAL);
  }

  public void newDay() {
    dailyUseItem.clear();
    dailyGainItem.clear();
    dailyBacklogItem.clear();
  }

  public void addGainItem(int itemId, int amount) {
    dailyGainItem.computeIfPresent(itemId, (k,v) -> v + amount);
    dailyBacklogItem.computeIfPresent(itemId, (k,v) -> v + amount);
  }

  public void addUseItem(int itemId, int amount) {
    dailyUseItem.computeIfPresent(itemId, (k,v) -> v + amount);
    dailyBacklogItem.computeIfPresent(itemId, (k,v) -> v - amount);
  }
}