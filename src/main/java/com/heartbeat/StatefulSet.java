package com.heartbeat;

import com.common.Constant;
import com.heartbeat.db.cb.AbstractCruder;
import com.heartbeat.db.dao.CommonEventDAO;
import com.heartbeat.scheduler.ExtendEventInfo;

import static com.heartbeat.HBServer.rxIndexBucket;

public class StatefulSet {
  private static AbstractCruder<CommonEventDAO> cbEvtAccess;
  private static final String evtKey = "serverEvent";

  static {
    cbEvtAccess = new AbstractCruder<>(CommonEventDAO.class, rxIndexBucket);
  }

  public static void loadCommonEvtFromDB() {
    CommonEventDAO dao = cbEvtAccess.load(evtKey);
    if (dao != null && dao.events != null)
      for (ExtendEventInfo eei : dao.events.values())
        Constant.COMMON_EVENT.evtMap.computeIfPresent(eei.eventId, (k,v) -> v = eei);
  }

  public static void syncCommonEvtToDB() {
    CommonEventDAO dao = CommonEventDAO.of(Constant.COMMON_EVENT.evtMap);
    cbEvtAccess.sync(evtKey, dao);
  }
}
