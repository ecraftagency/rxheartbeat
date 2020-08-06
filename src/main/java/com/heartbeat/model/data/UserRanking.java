package com.heartbeat.model.data;

import com.heartbeat.ranking.EventLoop;
import com.heartbeat.ranking.impl.FlushCommand;
import com.statics.RankingInfo;
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

import static com.heartbeat.common.Constant.RANKING.*;

public class UserRanking {
  public static final EventLoop rankingEventLoop;
  public static final Map<Integer, LeaderBoard<Integer, ScoreObj>> rankings;

  static {
    rankings          = new HashMap<>();
    rankings.putIfAbsent(TOTAL_TALENT_RANK_ID,  new LeaderBoard<>(LDB_CAPACITY));
    rankings.putIfAbsent(FIGHT_RANK_ID,         new LeaderBoard<>(LDB_CAPACITY));
    rankings.putIfAbsent(MONEY_SPEND_RANK_ID,   new LeaderBoard<>(LDB_CAPACITY));
    rankings.putIfAbsent(VIEW_SPEND_RANK_ID,    new LeaderBoard<>(LDB_CAPACITY));
    rankings.putIfAbsent(FAN_SPEND_RANK_ID,     new LeaderBoard<>(LDB_CAPACITY));

    rankingEventLoop  = new EventLoop();
    rankingEventLoop.run();
  }

  public Map<Integer, Long>       records;
  public int                      cas;
  public transient int            sessionId;
  public transient String         displayName;

  public static UserRanking ofDefault() {
    UserRanking ur  = new UserRanking();
    ur.records      = new HashMap<>();
    return ur;
  }

  public void addEventRecord(int rankingType, long amount) {
    RankingInfo ri = rankingInfo; //for short (rankingInfo is from constant)

    boolean active  = ri.activeRankings.getOrDefault(rankingType, false);

    LeaderBoard<Integer, ScoreObj> ldb = rankings.get(rankingType);
    if (ldb == null)
      return;

    if (cas != ri.startTime)
      records.computeIfPresent(rankingType, (k, v) -> v = 0L);

    int second    = (int)(System.currentTimeMillis()/1000);
    if (ri.startTime > 0 && active && second >= ri.startTime && second <= ri.endTime) {
      long oldVal = records.getOrDefault(rankingType, 0L);
      long newVal = oldVal + amount;

      EventLoop.Command record = new RecordCommand<>(ldb, ScoreObj.of(sessionId, newVal, displayName));
      rankingEventLoop.addCommand(record);
      records.put(rankingType, newVal);
      cas = ri.startTime;
    }
  }

  public void getRanking(int rankingType, Handler<AsyncResult<List<ScoreObj>>> ar) {
    LeaderBoard<Integer, ScoreObj> ldb = rankings.get(rankingType);
    if (ldb == null)
      ar.handle(Future.failedFuture("unknown_ranking_type"));

    EventLoop.Command listCommand = new ListCommand<>(ldb, ar);
    rankingEventLoop.addCommand(listCommand);
  }

  public static void flushAllRanking() {
    for (LeaderBoard<Integer, ScoreObj> ldb : rankings.values()) {
      EventLoop.Command flushCommand = new FlushCommand<>(ldb);
      rankingEventLoop.addCommand(flushCommand);
    }
  }
}