package com.statics;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MediaData {
  public static class Media implements Common.hasKey<Integer> {
    public int affairsID;
    public int questionID;
    public int rate;
    public String question;
    public String answer_1;
    public List<List<Integer>> reward_1;
    public String answer_2;
    public List<List<Integer>> reward_2;
    @Override
    public Integer mapKey() {
      return questionID;
    }
  }

  public static Map<Integer, Media> mediaMap;
  public static int totalRate;

  public static void loadJson(String mediaJson) {
    mediaMap = Common.loadMap(mediaJson, Media.class);
    for (Media media : mediaMap.values())
      totalRate += media.rate;
  }

  public static int nextRandQuestion() {
    int result = 1;
    try {
      int rate = getRandomNumberInRange(1, totalRate); //both side inclusive
      int acc = 0;
      for (Media media : mediaMap.values()) {
        acc += media.rate;
        if (acc >= rate) {
          result = media.questionID;
          break;
        }
      }
    }
    catch (Exception ignored) {
    }
    return result;
  }

  private static int getRandomNumberInRange(int min, int max) {

    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }

    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }
}
