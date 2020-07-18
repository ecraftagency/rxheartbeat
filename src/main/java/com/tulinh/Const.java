package com.tulinh;

import com.tulinh.dto.Item;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Const {
  public static       Map<Integer, String>  globalCounter;
  public static final JedisPoolConfig       poolConfig      = new JedisPoolConfig();
  public static       List<Item>            staticItems;
  public static       int                   SYNC_INTERVAL   = 4000; //milis
  public static       int                   nUSER = 5000;

  static {

    poolConfig.setMaxTotal(64);
    poolConfig.setMaxIdle(64);
    poolConfig.setMinIdle(16);
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
    poolConfig.setNumTestsPerEvictionRun(3);
    poolConfig.setBlockWhenExhausted(true);

    globalCounter = new HashMap<>();
    globalCounter.put(0, "10_counter");
    globalCounter.put(1, "50_counter");
    globalCounter.put(2, "mlg_counter");
    globalCounter.put(3, "mln_counter");
    globalCounter.put(4, "mq_counter");
    globalCounter.put(5, "mp_counter");
    globalCounter.put(6, "tlg_counter");
    globalCounter.put(7, "tln_counter");
    globalCounter.put(8, "tq_counter");
    globalCounter.put(9, "tp_counter");

    staticItems = new ArrayList<>();
    staticItems.add(Item.of(0, "The 10k", 500000, 8000, 0, true, -1, 0, -1, false));
    staticItems.add(Item.of(1, "The 50k", 500000, 1500, 0, true, -1, 0, -1, false));
    staticItems.add(Item.of(2, "Manh ghep Long", 500000, 90000, 0, false, 6, 0, 10, true));
    staticItems.add(Item.of(3, "Manh ghep Lan", 500000, 450000, 0, false, 7, 0, 10, true));
    staticItems.add(Item.of(4, "Manh ghep Quy", 500000, 450000, 0, false, 8, 0, 10, true));
    staticItems.add(Item.of(5, "Manh ghep Phung", 500000, 498, 0, false, 9, 0, 10, true));
    staticItems.add(Item.of(6, "The Long", 500000, 0, 0, false, -1, 0, -1, true));
    staticItems.add(Item.of(7, "The Lan", 500000, 1, 0, false, -1, 0, -1, true));
    staticItems.add(Item.of(8, "The Quy", 500000, 1, 0, false, -1, 0, -1, true));
    staticItems.add(Item.of(9, "The Phung", 500000, 0, 0, false, -1, 0, -1, true));
  }
}
