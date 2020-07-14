package group;

import com.couchbase.client.java.ReactiveCluster;
import com.heartbeat.HBServer;
import com.heartbeat.common.Constant;
import com.heartbeat.common.GlobalVariable;
import com.heartbeat.common.Utilities;
import com.heartbeat.db.cb.CBMapper;
import com.heartbeat.model.Session;
import com.heartbeat.model.data.UserGameInfo;
import com.transport.model.Group;

import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] args) {
    HBServer.rxCluster       = ReactiveCluster.connect(Constant.DB.HOST, Constant.DB.USER, Constant.DB.PWD);
    HBServer.rxSessionBucket = HBServer.rxCluster.bucket("sessions");
    HBServer.rxIndexBucket   = HBServer.rxCluster.bucket("index");
    HBServer.rxPersistBucket = HBServer.rxCluster.bucket("persist");

    Session session = Session.of(100004);
    session.userGameInfo = UserGameInfo.ofDefault();
    session.userGameInfo.displayName = "stalin";

    CBMapper.getInstance().map("10003", "100005");
    //createGroup(session);
    //createGroup(session);
    GlobalVariable.schThreadPool.scheduleAtFixedRate(() -> {}, 0, 1000000, TimeUnit.MINUTES);
  }

  public static void groupInfo(Session session) {
    session.loadSessionGroup(ar -> {
      if (ar.succeeded())
        System.out.println(Utilities.gson.toJson(ar.result()));
      else
        System.out.println(ar.cause().getMessage());
    });
  }

  public static void createGroup(Session session) {
    session.createGroup(Group.AUTO_JOIN, ar -> {
      if (ar.succeeded())
        System.out.println("ok");
      else
        System.out.println(ar.cause().getMessage());
    });
  }
}
