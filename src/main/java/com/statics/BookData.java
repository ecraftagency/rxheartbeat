package com.statics;

import java.util.HashMap;
import java.util.Map;

public class BookData{

  public static class Book implements Common.hasKey<Integer> {
    public int bookID;
    public String name;
    public int specialtyID;
    public int star;
    public int aptitude;
    public String content;

    @Override
    public Integer mapKey() {
      return bookID;
    }
  }

  public static Map<Integer, Book> books = new HashMap<>();

  public static void loadJson(String jsonText) {
    books = Common.loadMap(jsonText, Book.class);
  }
}