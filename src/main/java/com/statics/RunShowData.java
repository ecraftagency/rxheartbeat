package com.statics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RunShowData {
  public static class RunShow implements Common.hasKey<Integer> {
    public int            id;
    public long           minFanNPC;
    public long           maxFanNPC;
    public long           minAptNPC;
    public long           maxAptNPC;
    public long           randFanNPC;
    public long           randAptNPC;
    public List<Integer>  reward;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  @FunctionalInterface
  public interface MakeRand {
    void randProps(RunShow runShow);
  }

  public static Map<Integer, RunShow> runShowMap;
  public static MakeRand randomer = runShow -> {

  };

  public static List<List<Integer>> runShowRewards;
  public static List<Integer>       nItemPerShow;

  static {
    List<Integer> pack1 = Arrays.asList(19,20,28,29,60,35);
    List<Integer> pack2 = Arrays.asList(2,1,40,44,48,60,36);
    List<Integer> pack3 = Arrays.asList(64,70,71,72,61,5,40,44,48,22,31);
    List<Integer> pack4 = Arrays.asList(108,64,61,5,41,45,49,65,57,23,32);
    List<Integer> pack5 = Arrays.asList(108,57,65,66,42,46,50,5,91,24,33);
    runShowRewards      = Arrays.asList(pack1, pack2, pack3, pack4, pack5);
    nItemPerShow        = Arrays.asList(1,2,3,4,5);
  }

  public static void loadJson(String jsonText) {
    runShowMap = Common.loadMap(jsonText, RunShow.class);
  }

  public static RunShow of(int id) {
    RunShow rs = runShowMap.get(id);
    if (rs == null)
      return ofNullObject();
    RunShow clone = new RunShow();
    clone.id        = rs.id;
    clone.minFanNPC = rs.minFanNPC;
    clone.maxFanNPC = rs.maxFanNPC;
    clone.minAptNPC = rs.minAptNPC;
    clone.maxAptNPC = rs.maxAptNPC;
    clone.reward    = new ArrayList<>();
    clone.reward.addAll(rs.reward);
    randomer.randProps(clone);
    return clone;
  }

  private static RunShow ofNullObject() {
    RunShow rs = new RunShow();
    rs.id         = -1;
    rs.minAptNPC  = -1;
    rs.maxAptNPC  = -1;
    rs.minFanNPC  = -1;
    rs.maxFanNPC  = -1;
    return rs;
  }
}
