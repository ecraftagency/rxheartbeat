package com.heartbeat.db.dao;
import com.transport.model.LDBObj;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardDAO {
  public int          lastSync;
  public List<LDBObj> ldbData;

  public static LeaderBoardDAO ofDefault() {
    LeaderBoardDAO lbd  = new LeaderBoardDAO();
    lbd.lastSync        = 0;
    lbd.ldbData         = new ArrayList<>();
    return lbd;
  }

  public static LeaderBoardDAO of(List<LDBObj> ldbData) {
    LeaderBoardDAO dao = new LeaderBoardDAO();
    dao.ldbData = ldbData;
    dao.lastSync = (int)(System.currentTimeMillis()/1000);
    return dao;
  }
}