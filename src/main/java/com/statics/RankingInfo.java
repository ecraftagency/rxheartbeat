package com.statics;
import java.util.Map;

@SuppressWarnings("unused")
public class RankingInfo {
  public static final String  DATE_PATTERN            = "dd/MM/yyyy HH:mm:ss";
  public int      startTime;
  public int      endTime;
  public String   strStart;
  public String   strEnd;
  public int      flushDelay;
  public Map<Integer, Boolean> activeRankings;
}