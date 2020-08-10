package com.heartbeat.model.data;

import com.heartbeat.db.cb.CBLeaderBoard;
import com.heartbeat.db.dao.LeaderBoardDAO;
import com.heartbeat.model.Session;
import com.heartbeat.ranking.EventLoop;
import com.heartbeat.ranking.impl.LeaderBoard;
import com.heartbeat.ranking.impl.ListCommand;
import com.heartbeat.ranking.impl.RecordCommand;
import com.statics.LDBObj;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.*;

import static com.heartbeat.common.Constant.LEADER_BOARD.*;

public class UserLDB {
  private static final  EventLoop                                   ldbEventLoop;
  private static final  Map<Integer, LeaderBoard<Integer, LDBObj>>  leaderBoards;
  public                Session                                     session; //ref;

  static {
    ldbEventLoop = new EventLoop();
    leaderBoards = new HashMap<>();
    leaderBoards.put(TALENT_LDB_ID, new LeaderBoard<Integer, LDBObj>(LDB_CAPACITY, new ArrayList<>(), false));
    leaderBoards.put(FIGHT_LDB_ID, new LeaderBoard<Integer, LDBObj>(LDB_CAPACITY, new ArrayList<>(), false));
    ldbEventLoop.run();
  }

  public static void loadLDBFromDB(int ldbType) {
    id2key.computeIfPresent(ldbType, (k,v) -> {
      LeaderBoardDAO dao = CBLeaderBoard.getInstance().load(v);
      leaderBoards.put(ldbType, new LeaderBoard<>(LDB_CAPACITY, dao.ldbData, false));
      return v;
    });
  }

  public static void syncLDBToDB(int ldbType) { //todo synchronize
    LeaderBoard<Integer, LDBObj> ldb = leaderBoards.get(ldbType);
    if (ldb == null)
      return;

    id2key.computeIfPresent(ldbType, (k, v) -> {
      //synchronized (LeaderBoard.class) {
      List<LDBObj> ldbData = ldb.get();
      LeaderBoardDAO dao = LeaderBoardDAO.of(ldbData);
      CBLeaderBoard.getInstance().sync(v, dao);
      //}
      return v;
    });
  }

  /********************************************************************************************************************/

  public static UserLDB of(Session session) {
    UserLDB sLDB = new UserLDB();
    sLDB.session = session;
    return sLDB;
  }

  public void addLdbRecord(int ldbType, long score) {
    if (session == null)
      return;
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