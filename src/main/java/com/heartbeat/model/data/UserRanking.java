package com.heartbeat.model.data;

import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.heartbeat.ranking.EventLoop;
import com.heartbeat.ranking.impl.*;
import com.statics.RankingData;
import com.statics.RankingInfo;
import com.transport.model.ScoreObj;
import com.transport.model.Ranking;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.common.Constant.RANKING.*;

public class UserRanking extends Ranking {
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

  public transient int            sessionId;
  public transient String         displayName;

  public static UserRanking ofDefault() {
    UserRanking ur  = new UserRanking();
    ur.records      = new HashMap<>();
    ur.claimed      = new HashMap<>();
    return ur;
  }

  public static void flushAllRanking() {
    for (LeaderBoard<Integer, ScoreObj> ldb : rankings.values()) {
      EventLoop.Command flushCommand = new FlushCommand<>(ldb);
      rankingEventLoop.addCommand(flushCommand);
    }
  }

  public static void closeAllRanking() {
    for (LeaderBoard<Integer, ScoreObj> ldb : rankings.values()) {
      EventLoop.Command closeCommand = new CloseCommand<>(ldb);
      rankingEventLoop.addCommand(closeCommand);
    }
  }

  public static void openAllRanking() {
    for (LeaderBoard<Integer, ScoreObj> ldb : rankings.values()) {
      EventLoop.Command openCommand = new OpenCommand<>(ldb);
      rankingEventLoop.addCommand(openCommand);
    }
  }

  /********************************************************************************************************************/

  public void reBalance() {
    if (claimed == null)
      claimed = new HashMap<>();
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

  public void claimReward(Session session, int rankingType, Handler<AsyncResult<String>> ar) {
    LeaderBoard<Integer, ScoreObj> ldb = rankings.get(rankingType);
    if (ldb == null) {
      ar.handle(Future.failedFuture("unknown_ranking_type"));
      return;
    }

    Map<Integer, RankingData.RewardDto> rewardMap = RankingData.rewardMap.get(rankingType);
    if (rewardMap == null) {
      ar.handle(Future.failedFuture("rewards_data_not_found"));
      return;
    }

    RankingInfo ri  = rankingInfo; //for short (rankingInfo is from constant)
    boolean active  = ri.activeRankings.getOrDefault(rankingType, false);

    if (!active) {
      ar.handle(Future.failedFuture("ranking_not_active"));
      return;
    }

    int cas = claimed.getOrDefault(rankingType, 0);
    if (cas == ri.startTime) {
      ar.handle(Future.failedFuture("already_claim"));
      return;
    }

    int second      = (int)(System.currentTimeMillis()/1000);
    if (ri.startTime <= 0 || second <= ri.startTime || second >= ri.endTime + FLUSH_DELAY){
      ar.handle(Future.failedFuture("claim_time_out"));
      return;
    }

    EventLoop.Command rankCommand = new GetRankCommand<>(ldb, session.id, rar -> {
      if (rar.succeeded()) {
        int rank = rar.result();
        if (rank < 1 || rank > 100) {
          ar.handle(Future.failedFuture("invalid_rank"));
        }
        else {
          claimed.put(rankingType, ri.startTime);
          RankingData.RewardDto dto = rewardMap.get(rank);
          if (dto == null) {
            ar.handle(Future.failedFuture("invalid_rank"));
            return;
          }

          for (List<Integer> r : dto.reward) {
            EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, r);
          }

          ar.handle(Future.succeededFuture("ok")); //todo ok here :)
          //reward finally
        }
      }
      else {
        ar.handle(Future.failedFuture("unknown_err"));
      }
    });
    rankingEventLoop.addCommand(rankCommand);
  }

  public void getRanking(int rankingType, Handler<AsyncResult<List<ScoreObj>>> ar) {
    LeaderBoard<Integer, ScoreObj> ldb = rankings.get(rankingType);
    if (ldb == null)
      ar.handle(Future.failedFuture("unknown_ranking_type"));

    EventLoop.Command listCommand = new ListCommand<>(ldb, ar);
    rankingEventLoop.addCommand(listCommand);
  }

  public void getAllRanking(Session session, Handler<AsyncResult<Map<Integer, Integer>>> ar) {
    EventLoop.Command getAllRankCmd = new ListAllRankCommand<>(session.id, rankings, ar);
    rankingEventLoop.addCommand(getAllRankCmd);
  }
}