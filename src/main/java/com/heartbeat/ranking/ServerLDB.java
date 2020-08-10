package com.heartbeat.ranking;

import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.kv.GetResult;
import com.heartbeat.model.Session;
import com.heartbeat.ranking.impl.LeaderBoard;
import com.heartbeat.ranking.impl.ListCommand;
import com.heartbeat.ranking.impl.RecordCommand;
import com.statics.LDBObj;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.heartbeat.common.Constant.LEADER_BOARD.*;

public class ServerLDB {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServerLDB.class);

  private static final EventLoop                                  ldbEventLoop;
  private static final Map<Integer, LeaderBoard<Integer, LDBObj>> leaderBoards;
  public  static ReactiveBucket                                   rxIndexBucket;
  public Session session; //ref;

  static {
    ldbEventLoop = new EventLoop();
    leaderBoards = new HashMap<>();
    leaderBoards.put(TALENT_LDB_ID, new LeaderBoard<Integer, LDBObj>(LDB_CAPACITY, new ArrayList<>(), false));
    leaderBoards.put(FIGHT_LDB_ID, new LeaderBoard<Integer, LDBObj>(LDB_CAPACITY, new ArrayList<>(), false));
    ldbEventLoop.run();
  }

  public static void loadLDBFromDB(int ldbType) {
    String key;
    if (ldbType == TALENT_LDB_ID)
      key = TALENT_LDB_KEY;
    else if (ldbType == FIGHT_LDB_ID)
      key = FIGHT_LDB_KEY;
    else {
      leaderBoards.put(ldbType, new LeaderBoard<>(LDB_CAPACITY, new ArrayList<>(), false));
      return;
    }

    try {
      GetResult result = rxIndexBucket.defaultCollection().get(key).block();
      if (result == null) {
        leaderBoards.put(ldbType, new LeaderBoard<>(LDB_CAPACITY, new ArrayList<>(), false));
      }
      else {
        List<LDBObj> ldbResult = Arrays.asList(result.contentAs(LDBObj[].class));
        leaderBoards.put(ldbType, new LeaderBoard<>(LDB_CAPACITY, ldbResult, false));
      }
    }
    catch (Exception e) {
      leaderBoards.put(ldbType, new LeaderBoard<>(LDB_CAPACITY, new ArrayList<>(), false));
      LOGGER.error(e.getMessage());
    }
  }

  public static void syncLDBToDB(int ldbType) {
    String key;
    if (ldbType == TALENT_LDB_ID)
      key = TALENT_LDB_KEY;
    else if (ldbType == FIGHT_LDB_ID)
      key = FIGHT_LDB_KEY;
    else
      return;

    synchronized (LeaderBoard.class) {
      LeaderBoard<Integer, LDBObj> ldb = leaderBoards.get(ldbType);
      if (ldb == null)
        return;
      List<LDBObj> ldbObj = ldb.get();
      LDBObj[] ldbObjArr  = ldbObj.toArray(new LDBObj[]{});
      try {
        rxIndexBucket.defaultCollection().upsert(key, ldbObjArr);
      }
      catch (Exception e) {
        LOGGER.error(e.getMessage());
      }
    }
  }

  /********************************************************************************************************************/

  public static ServerLDB of(Session session) {
    ServerLDB sLDB = new ServerLDB();
    sLDB.session = session;
    return sLDB;
  }

  public void addLdbRecord(int ldbType, long score) {
    LeaderBoard<Integer, LDBObj> ldb = leaderBoards.get(ldbType);
    if (ldb == null)
      return;
    EventLoop.Command recordCommand = new RecordCommand<>(ldb, LDBObj.of(
              session.id,
              session.userGameInfo.displayName,
              session.userGameInfo.titleId,
              score
            ));

    ldbEventLoop.addCommand(recordCommand);
  }

  public void getLeaderBoard(int ldbType, Handler<AsyncResult<List<LDBObj>>> ar) {
    LeaderBoard<Integer, LDBObj> ldb = leaderBoards.get(ldbType);
    if (ldb == null)
      ar.handle(Future.failedFuture("unknown_ranking_type"));

    EventLoop.Command listCommand = new ListCommand<>(ldb, ar);
    ldbEventLoop.addCommand(listCommand);
  }
}