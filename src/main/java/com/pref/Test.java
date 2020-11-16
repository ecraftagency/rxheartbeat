package com.pref;

import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.ReactiveCluster;
import java.util.HashMap;
import java.util.Map;

public class Test {
  public static ReactiveCluster                   rxCluster;
  public static Map<Integer, ReactiveBucket>      refBuckets;

  public static void main(String[] args) throws InterruptedException {
    rxCluster     = ReactiveCluster.connect("13.212.159.107", "Administrator", "n5t5lnsct");
    refBuckets    = new HashMap<>();
    refBuckets.put(1, rxCluster.bucket(String.format("ref_%d", 1)));
    refBuckets.put(2, rxCluster.bucket(String.format("ref_%d", 2)));
    refBuckets.put(3, rxCluster.bucket(String.format("ref_%d", 3)));
    refBuckets.put(4, rxCluster.bucket(String.format("ref_%d", 4)));
    refBuckets.put(5, rxCluster.bucket(String.format("ref_%d", 5)));
    refBuckets.put(6, rxCluster.bucket(String.format("ref_%d", 6)));
    refBuckets.put(7, rxCluster.bucket(String.format("ref_%d", 7)));
    refBuckets.put(8, rxCluster.bucket(String.format("ref_%d", 8)));
    PrefService svc = new CBPrefImpl(refBuckets);

    svc.addProfile("9000000", 1000000, ar -> {
      if (ar.succeeded())
        System.out.println(ar.result());
      else
        System.out.println(ar.cause().getMessage());
    });


    Thread.sleep(5000);

//    DB db = DBMaker
//            .fileDB("ref.db")
//            .fileMmapEnable()
//            .fileMmapEnableIfSupported()
//            .fileMmapPreclearDisable()
//            .cleanerHackEnable().make();
//    ConcurrentMap<Long, Long> persistence = db.hashMap("map").keySerializer(Serializer.LONG).valueSerializer(Serializer.LONG).createOrOpen();
//
//    persistence.putIfAbsent(10000L, 2000L);
//    System.out.println(persistence.getOrDefault(10000L,0L));
//    db.close();
//  }
//
//  public static class Identity {
//    public long           id; //phoenix id
//    public Set<Long>      downLinkAccounts;
//    public long           upLinkAccount;
//    public Set<Long>      profiles;
//    public boolean        claimLinkReward;
//
//    public static Identity ofDefault(long id) {
//      Identity i          = new Identity();
//      i.id                = id;
//      i.downLinkAccounts  = new HashSet<>();
//      i.upLinkAccount     = 0;
//      i.profiles          = new HashSet<>();
//      i.claimLinkReward   = false;
//      return i;
//    }
  }
}