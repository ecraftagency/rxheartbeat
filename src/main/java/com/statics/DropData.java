package com.statics;

import com.heartbeat.common.Utilities;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropData {
  public static class Drop implements Common.hasKey<Integer> {
    public int              id;
    public String           description;
    public int              count;
    public int              type;
    public List<Integer>    item_1;
    public int              rate_1;
    public List<Integer>    item_2;
    public int              rate_2;
    public List<Integer>    item_3;
    public int              rate_3;
    public List<Integer>    item_4;
    public int              rate_4;
    public List<Integer>    item_5;
    public int              rate_5;
    public List<Integer>    item_6;
    public int              rate_6;
    public List<Integer>    item_7;
    public int              rate_7;
    public List<Integer>    item_8;
    public int              rate_8;
    public List<Integer>    item_9;
    public int              rate_9;
    public List<Integer>    item_10;
    public int              rate_10;
    public List<Integer>    item_11;
    public int              rate_11;
    public List<Integer>    item_12;
    public int              rate_12;
    public List<Pack>       rewardPacks;
    public int              totalRate;

    @Override
    public Integer mapKey() {
      return id;
    }
  }

  public static class Pack {
    public List<Integer> format;
    public int           rate;
    public static Pack of (List<Integer> format, int rate) {
      Pack pack = new Pack();
      pack.format = format;
      pack.rate   = rate;
      return pack;
    }
  }

  public static Map<Integer, Drop> dropMap;

  public static void loadJson(String jsonText) {
    dropMap = new HashMap<>();
    try {
      JSONArray rows = new JSONArray(jsonText);
      for (int i = 0; i < rows.length(); i++){
        JSONObject row = rows.getJSONObject(i);

        for (int j = 1; j <= 12; j++) {
          if (row.getString("item_" + j).equals(""))
            row.put("item_" + j, "[]");
          if (row.getString("rate_" + j).equals(""))
            row.put("rate_" + j, "0");
        }

        Common.arraySubstitute(rows.getJSONObject(i));

        Drop drop = Utilities.gson.fromJson(row.toString(), Drop.class);
        dropMap.put(drop.id, drop);
      }
    }
    catch (Exception e) {
      dropMap.clear();
      return;
    }


    //dropMap = Common.loadMap(jsonText, Drop.class);
    for (Drop drop : dropMap.values()) {
      drop.rewardPacks = new ArrayList<>();
      drop.totalRate   = 0;
      if (drop.item_1.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_1, drop.rate_1));
        drop.totalRate += drop.rate_1;
      }
      if (drop.item_2.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_2, drop.rate_2));
        drop.totalRate += drop.rate_2;
      }
      if (drop.item_3.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_3, drop.rate_3));
        drop.totalRate += drop.rate_3;
      }
      if (drop.item_4.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_4, drop.rate_4));
        drop.totalRate += drop.rate_4;
      }
      if (drop.item_5.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_5, drop.rate_5));
        drop.totalRate += drop.rate_5;
      }
      if (drop.item_6.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_6, drop.rate_6));
        drop.totalRate += drop.rate_6;
      }
      if (drop.item_7.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_7, drop.rate_7));
        drop.totalRate += drop.rate_7;
      }
      if (drop.item_8.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_8, drop.rate_8));
        drop.totalRate += drop.rate_8;
      }
      if (drop.item_9.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_9, drop.rate_9));
        drop.totalRate += drop.rate_9;
      }
      if (drop.item_10.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_10, drop.rate_10));
        drop.totalRate += drop.rate_10;
      }
      if (drop.item_11.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_11, drop.rate_11));
        drop.totalRate += drop.rate_11;
      }
      if (drop.item_12.size() == 4) {
        drop.rewardPacks.add(Pack.of(drop.item_12, drop.rate_12));
        drop.totalRate += drop.rate_12;
      }
    }
  }
}
