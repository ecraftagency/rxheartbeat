package com.tulinh.dto;

public class History {
  public int      type;
  public String   name;
  public long     date;

  public static History of(int type, String name, long date) {
    History res = new History();
    res.type  = type;
    res.name  = name;
    res.date  = date;
    return res;
  }
}