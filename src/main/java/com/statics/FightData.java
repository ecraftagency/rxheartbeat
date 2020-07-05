package com.statics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FightData {
  public static class FightLV implements Common.hasKey<Integer> {
    public int                      id;
    public int                      chapter;
    public int                      level;
    public int                      smallLevel;
    public long                     aptNPC;
    public long                     fanNPC;
    public List<Integer>            reward;
    public FightBossData.FightBoss  boss;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, FightLV> fightMap;

  public static void loadJson(String jsonText) {
    fightMap = Common.loadMap(jsonText, FightLV.class);
    for (FightLV fightLV : fightMap.values()) {
      if (fightLV.level == 6) {
        fightLV.boss = FightBossData.fightBossMap.get(fightLV.chapter);
      }
    }
  }

  public static FightLV of(int id) {
    FightLV fightLV = fightMap.get(id);
    if (fightLV == null)
      return null;
    FightLV clone = new FightLV();
    clone.id = fightLV.id;
    clone.chapter = fightLV.chapter;
    clone.level = fightLV.level;
    clone.smallLevel = fightLV.smallLevel;
    clone.aptNPC = fightLV.aptNPC;
    clone.fanNPC = fightLV.fanNPC;
    clone.reward = new ArrayList<>();
    clone.reward.addAll(fightLV.reward);
    if (fightLV.boss != null) {
      clone.boss = new FightBossData.FightBoss();
      clone.boss.id = fightLV.boss.id;
      clone.boss.chapter  = fightLV.boss.chapter;
      clone.boss.hp = fightLV.boss.hp;
      clone.boss.reward = new ArrayList<>();
      clone.boss.reward.addAll(fightLV.boss.reward);
    }
    return clone;
  }
}