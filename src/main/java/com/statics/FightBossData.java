package com.statics;

import java.util.List;
import java.util.Map;

public class FightBossData {
  public static class FightBoss implements Common.hasKey<Integer> {
    //id,chapter,hp,reward
    public int id;
    public int chapter;
    public long hp;
    public List<Integer> reward;
    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, FightBoss> fightBossMap;

  public static void loadJson(String jsonText) {
    fightBossMap = Common.loadMap(jsonText, FightBoss.class);
  }
}
