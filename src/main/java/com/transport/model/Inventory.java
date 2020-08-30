package com.transport.model;

import java.util.HashMap;
import java.util.LinkedList;

public class Inventory {
  public HashMap<Integer, Integer>              userItems;
  public HashMap<Integer, LinkedList<Integer>>  expireItems;
}