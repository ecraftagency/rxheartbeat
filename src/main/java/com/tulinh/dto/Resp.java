package com.tulinh.dto;

public class Resp {
  public static class ItemFail {
    public String result;
    public int turn;
    public static ItemFail of(String result, int turn) {
      ItemFail res = new ItemFail();
      res.result = result;
      res.turn = turn;
      return res;
    }
  }

  public static class ItemOk {
    public int type;
    public String name;
    public int turn;

    public static ItemOk of(int type, String name, int turn) {
      ItemOk res = new ItemOk();
      res.type = type;
      res.name = name;
      res.turn = turn;
      return res;
    }
  }
}
