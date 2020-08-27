package com.statics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameShowData {
  public static class GameShow implements Common.hasKey<Integer> {
    public int id;
    public long bosshp;
    public List<List<Integer>> reward;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static Map<Integer, GameShow> gameShowMap;

  public static void loadJson(String jsonText) {
    gameShowMap = Common.loadMap(jsonText, GameShow.class);
  }

  public static GameShow of(int id) {
    GameShow gs = gameShowMap.get(id);
    if (gs == null)
      return null;
    GameShow clone = new GameShow();
    clone.id = gs.id;
    clone.bosshp = gs.bosshp;
    clone.reward = new ArrayList<>();
    clone.reward.addAll(gs.reward);
    return clone;
  }

  public static GameShow ofNullObject() {
    GameShow gs = new GameShow();
    gs.reward = new ArrayList<>();
    gs.id = -1;
    gs.bosshp = -1;
    return gs;
  }
}