package com.transport.model;

import java.util.Map;

public class Ranking {
  public Map<Integer, Long>       records;
  public Map<Integer, Integer>    claimed; //value of claimed is start time of the event :)
  public Map<Integer, Integer>    evt2cas;
}