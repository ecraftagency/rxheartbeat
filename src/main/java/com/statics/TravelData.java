package com.statics;

import java.util.List;
import java.util.Map;

public class TravelData {
  public static class TravelNPC implements Common.hasKey<Integer> {
    public int            id;
    public int            structure;
    public String         name;
    public int            type;
    public String         title;
    public String         desc;
    public List<Integer>  reward;

    public int getType() {
      return type;
    }

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, TravelNPC> travelNPCMap;
  public static Map<Integer, List<TravelNPC>> npcTypeMap;

  public interface CustomAction {
    void doAction();
  }

  public static void loadJson(String jsonText, CustomAction ca) {
    travelNPCMap = Common.loadMap(jsonText, TravelNPC.class);
    ca.doAction();
  }
}
