package com.transport.model;

import java.util.HashMap;
import java.util.List;

public class Inventory {
  public HashMap<Integer, Integer>              userItems;
  public HashMap<Integer, List<Integer>>        expireItems;
}