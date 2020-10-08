package com.heartbeat.model.data;

import com.common.Constant;
import com.common.Msg;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.heartbeat.ranking.EventLoop;
import com.heartbeat.ranking.impl.*;
import com.statics.EventInfo;
import com.statics.RankingData;
import com.transport.model.ScoreObj;
import com.transport.model.Ranking;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.common.Constant.RANK_EVENT.*;

@JsonIgnoreProperties(ignoreUnknown = true)
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
    ur.evt2cas      = new HashMap<>();
    return ur;
  }

  /********************************************************************************************************************/

  public void reBalance() {
    if (claimed == null)
      claimed = new HashMap<>();
    if (evt2cas == null)
      evt2cas = new HashMap<>();

    for (Integer rankId : evtMap.keySet()) {
      records.putIfAbsent(rankId, 0L);
      claimed.putIfAbsent(rankId, 0);
      evt2cas.putIfAbsent(rankId, 0);

      EventInfo ri = Constant.RANK_EVENT.evtMap.get(rankId);
      if (ri != null && invalidCas(rankId, ri.startTime))
        resetEventData(rankId);
    }
  }

  private void resetEventData(int evtId) {
    records.computeIfPresent(evtId ,(k, v) -> v *= 0);
    claimed.computeIfPresent(evtId, (k, v) -> v = 0);
    evt2cas.computeIfPresent(evtId, (k, v) -> v = 0); //reset cas to zero
  }

  private boolean invalidCas(int evtId, int cas) {
    int oldCas = evt2cas.getOrDefault(evtId, 0);
    return oldCas != cas;
  }

  public void addEventRecord(int rankId, long amount) {
    EventInfo ri = evtMap.get(rankId);
    if (ri == null)
      return;

    LeaderBoard<Integer, ScoreObj> ldb = rankings.get(rankId);
    if (ldb == null)
      return;

    if (invalidCas(rankId, ri.startTime)) {
      resetEventData(rankId);
    }

    int second    = (int)(System.currentTimeMillis()/1000);
    if (ri.startTime > 0 && ri.active && second >= ri.startTime && second <= ri.endTime) {
      long oldVal = records.getOrDefault(rankId, 0L);
      long newVal = oldVal + amount;

      EventLoop.Command record = new RecordCommand<>(ldb, ScoreObj.of(sessionId, newVal, displayName));
      rankingEventLoop.addCommand(record);
      records.put(rankId, newVal);  //update new record
      evt2cas.put(rankId, ri.startTime); //update cas if !=
    }
  }

  public void claimReward(Session session, int rankId, Handler<AsyncResult<String>> ar) {
    LeaderBoard<Integer, ScoreObj> ldb = rankings.get(rankId);
    if (ldb == null) {
      ar.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_RANK_TYPE, "unknown_rank_type")));
      return;
    }

    Map<Integer, RankingData.RewardDto> rewardMap = RankingData.rewardMap.get(rankId);
    if (rewardMap == null) {
      ar.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.DTO_DATA_NOT_FOUND,"rewards_data_not_found")));
      return;
    }

    EventInfo ri  = evtMap.get(rankId);
    if (ri == null) {
      ar.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.EVENT_NOT_FOUND, "event_not_found")));
      return;
    }

    if (!ri.active) {
      ar.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.RANKING_NOT_ACTIVE, "ranking_not_active")));
      return;
    }

    int claimCas = claimed.getOrDefault(rankId, 0);
    if (claimCas == ri.startTime) {
      ar.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.ALREADY_CLAIM, "already_claim")));
      return;
    }

    int second      = (int)(System.currentTimeMillis()/1000);
    if (ri.startTime <= 0 || second <= ri.startTime || second >= ri.endTime + ri.flushDelay){
      ar.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.TIMEOUT_CLAIM, "timeout_claim")));
      return;
    }

    EventLoop.Command rankCommand = new GetRankCommand<>(ldb, session.id, rar -> {
      if (rar.succeeded()) {
        int rank = rar.result();
        if (rank < 1 || rank > 100) {
          ar.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.INVALID_RANK, "invalid_rank")));
        }
        else {
          claimed.put(rankId, ri.startTime);
          RankingData.RewardDto dto = rewardMap.get(rank);
          if (dto == null) {
            ar.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.INVALID_RANK, "invalid_rank")));
            return;
          }

          List<List<Integer>> reward = ri.rewardPack == 2 ? dto.reward2 : dto.reward1;
          for (List<Integer> r : reward) {
            EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, r);
          }

          ar.handle(Future.succeededFuture("ok")); //todo ok here :)
          //reward finally
        }
      }
      else {
        ar.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_ERR, "unknown_err")));
      }
    });
    rankingEventLoop.addCommand(rankCommand);
  }

  public void getRanking(int rankingType, Handler<AsyncResult<List<ScoreObj>>> ar) {
    LeaderBoard<Integer, ScoreObj> ldb = rankings.get(rankingType);
    if (ldb == null)
      ar.handle(Future.failedFuture(Msg.map.getOrDefault(Msg.UNKNOWN_RANK_TYPE, "unknown_rank_type")));

    EventLoop.Command listCommand = new ListCommand<>(ldb, ar);
    rankingEventLoop.addCommand(listCommand);
  }

  public void getAllRanking(Session session, Handler<AsyncResult<Map<Integer, Integer>>> ar) {
    EventLoop.Command getAllRankCmd = new ListAllRankCommand<>(session.id, rankings, ar);
    rankingEventLoop.addCommand(getAllRankCmd);
  }
}