package com.heartbeat.event;

import com.common.LOG;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.heartbeat.HBServer;
import com.heartbeat.scheduler.ExtendEventInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Common {
  public static final int EVENT_DISTANCE = 120; //time between 2 adjacent events in seconds

  public static class ScheduleEvt {
    public int      id;
    public int      eventId;
    public String   startDate;
    public String   endDate;
    public int      flushDelay;
    public Integer  rewardPack;

    public int getEventId() {
      return eventId;
    }
  }

  public static String updatePlan(Map<Integer,List<ExtendEventInfo>> curPlan, String csvPlan) {
    List<ScheduleEvt> res;
    try {
      CsvSchema bootstrap = CsvSchema.emptySchema().withHeader();
      CsvMapper csvMapper = new CsvMapper();
      MappingIterator<ScheduleEvt> mappingIterator = csvMapper.reader(ScheduleEvt.class).with(bootstrap)
              .readValues(csvPlan);
      res = mappingIterator.readAll();
    }
    catch (Exception e) {
      LOG.globalException(String.format("node_%d", HBServer.nodeId), "CommonEvent.updatePlan", e);
      return e.getMessage();
    }

    if (res == null)
      return "empty_event_list";

    Map<Integer, List<ScheduleEvt>> evt2List =
            res.stream().collect(Collectors.groupingBy(ScheduleEvt::getEventId, Collectors.toList()));

    Map<Integer, List<ExtendEventInfo>> newPlan = new HashMap<>();
    for (Map.Entry<Integer, List<ScheduleEvt>> subPlan : evt2List.entrySet()) {
      List<ExtendEventInfo> evtList = new ArrayList<>();
      for (ScheduleEvt scheduleEvt : subPlan.getValue()) {
        ExtendEventInfo evt = ExtendEventInfo.of(scheduleEvt.eventId, scheduleEvt.rewardPack);
        if (evt.updateTime(scheduleEvt.startDate, scheduleEvt.endDate, scheduleEvt.flushDelay, scheduleEvt.rewardPack))
          evtList.add(evt);
        else
          return String.format("invalid_event[eventId:%d,startDate:%s,endDate%s]",
                  scheduleEvt.eventId, scheduleEvt.startDate, scheduleEvt.endDate);
      }
      newPlan.put(subPlan.getKey(), evtList);
    }

    for (Map.Entry<Integer, List<ExtendEventInfo>> entry : newPlan.entrySet()) {
      List<ExtendEventInfo> subPlan = entry.getValue();
      if (subPlan.size() >= 2) {
        int dt;
        for (int i = 0; i < subPlan.size() - 1; i++) {
          dt = subPlan.get(i + 1).startTime - (subPlan.get(i).endTime + subPlan.get(i).flushDelay);
          if (dt < EVENT_DISTANCE)
            return String.format("event_plan_inconsistent[eventId:%d]", entry.getKey());
        }
      }
    }

    synchronized (Common.class) {
      for (Map.Entry<Integer, List<ExtendEventInfo>> entry : newPlan.entrySet())
        curPlan.put(entry.getKey(), entry.getValue());
    }
    return "ok";
  }
}