package com.heartbeat.model.data;

import com.heartbeat.model.Session;
import com.heartbeat.ranking.EventLoop;
import com.heartbeat.ranking.ScoreObj;
import com.heartbeat.ranking.impl.LeaderBoard;
import com.heartbeat.ranking.impl.RecordCommand;

import java.util.HashMap;
import java.util.Map;

public class UserRanking {
  public static final LeaderBoard<Integer, ScoreObj>    talentRanking;
  public static final EventLoop                         rankingEventLoop;
  public static final int LDB_CAPACITY = 100;

  static {
    talentRanking     = new LeaderBoard<>(LDB_CAPACITY);
    rankingEventLoop  = new EventLoop();
    rankingEventLoop.run();
  }

  public Map<Integer, Long>       records;
  public Map<Integer, Integer>    evt2cas;
  public transient int            sessionId;
  public transient String         displayName;

  public static UserRanking ofDefault() {
    UserRanking ur  = new UserRanking();
    ur.records      = new HashMap<>();
    ur.evt2cas      = new HashMap<>();
    return ur;
  }

  public void addEventRecord(int eventType, long amount) {
    long oldVal = records.getOrDefault(eventType, 0L);
    long newVal = oldVal + amount;
    EventLoop.Command record = new RecordCommand<>(talentRanking, ScoreObj.of(sessionId, newVal, displayName));
    rankingEventLoop.addCommand(record);
    records.put(eventType, newVal);
  }
}