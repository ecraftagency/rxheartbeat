package com.tulinh.dto;

import java.util.List;

public class User {
  public String token;
  public int turns;
  public String card_billion;
  public List<EventDonate> event_donate;
  public List<Inventory> inventories;
  public List<History> histories;
  public List<EventMerge> event_merge;
}