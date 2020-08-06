package com.statics;

public class ScoreObj implements Comparable<ScoreObj>, Common.hasKey<Integer> {
  public int    id;
  public long   score;
  public String name;

  public static ScoreObj of(int id, long score, String name) {
    ScoreObj scoreObj = new ScoreObj();
    scoreObj.id = id;
    scoreObj.score = score;
    scoreObj.name = name;
    return scoreObj;
  }

  @Override
  public int compareTo(ScoreObj o) {
    long c = score - o.score;
    if (c > 0)
      return 1;
    else if (c < 0)
      return -1;
    return 0;
  }

  @Override
  public Integer mapKey() {
    return id;
  }
}
