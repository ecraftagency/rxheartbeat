package com.heartbeat.model.data;

import com.heartbeat.common.Constant;
import com.heartbeat.ranking.EventLoop;
import com.statics.ScoreObj;
import com.heartbeat.ranking.impl.LeaderBoard;
import com.heartbeat.ranking.impl.ListCommand;
import com.heartbeat.ranking.impl.RecordCommand;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRanking {
  public static final EventLoop rankingEventLoop;
  public static final int LDB_CAPACITY = 100;
  public static final Map<Integer, LeaderBoard<Integer, ScoreObj>> rankings;

  static {
    rankings          = new HashMap<>();
    rankings.putIfAbsent(Constant.RANKING.TOTAL_TALENT_RANK_ID, new LeaderBoard<>(LDB_CAPACITY));
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

  public void addEventRecord(int rankingType, long amount) {
    LeaderBoard<Integer, ScoreObj> ldb = rankings.get(rankingType);
    if (ldb == null)
      return;

    long oldVal = records.getOrDefault(rankingType, 0L);
    long newVal = oldVal + amount;

    EventLoop.Command record = new RecordCommand<>(ldb, ScoreObj.of(sessionId, newVal, displayName));
    rankingEventLoop.addCommand(record);
    records.put(rankingType, newVal);
  }

  public void getRanking(int rankingType, Handler<AsyncResult<List<ScoreObj>>> ar) {
    LeaderBoard<Integer, ScoreObj> ldb = rankings.get(rankingType);
    if (ldb == null)
      ar.handle(Future.failedFuture("unknown_ranking_type"));

    EventLoop.Command listCommand = new ListCommand<>(ldb, ar);
    rankingEventLoop.addCommand(listCommand);
  }
}