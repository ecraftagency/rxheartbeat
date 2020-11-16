package com.statics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EventData {
  public static class EventDto implements Common.hasKey<Integer> {
    public int                  id;
    public int                  eventType;
    public int                  milestoneId;
    public long                 milestoneValue;
    public String               title;
    public String               rewardDesc;
    public List<List<Integer>>  reward1;
    public List<List<Integer>>  reward2;
    public List<List<Integer>>  reward3;
    public List<List<Integer>>  reward4;

    @Override
    public Integer mapKey() {
      return id;
    }

    public List<List<Integer>> getRewardPack(int packId) {
      switch (packId){
        case 2:
          return reward2;
        case 3:
          return reward3;
        case 4:
          return reward4;
        default:
          return reward1;
      }
    }
  }

  public static Map<Integer, EventDto>                eventDtoMap;
  public static Map<Integer, Map<Integer, EventDto>>  eventMap;

  public static void loadJson(String jsonText) {
    eventDtoMap = Common.loadMap(jsonText, EventDto.class);
    eventMap    = new HashMap<>();
    for (EventDto dto : eventDtoMap.values()) {
      if (!eventMap.containsKey(dto.eventType)) {
        eventMap.put(dto.eventType, new TreeMap<>());
      }
      eventMap.get(dto.eventType).put(dto.milestoneId, dto);
    }
  }
}