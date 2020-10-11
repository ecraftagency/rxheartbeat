package com.heartbeat.scheduler;

import com.common.Constant;
import com.common.LOG;
import com.diabolicallabs.vertx.cron.CronObservable;
import com.heartbeat.model.GroupPool;
import com.heartbeat.model.SessionPool;
import com.heartbeat.model.data.UserInventory;
import com.heartbeat.model.data.UserRanking;
import com.heartbeat.ranking.EventLoop;
import com.heartbeat.ranking.impl.TimeCheckCommand;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.RxHelper;

import static com.common.Constant.TIME_ZONE;
import static com.heartbeat.HBServer.*;

public class TaskRunner {
  public interface ScheduleAble {
    void updateTime(String startDate, String endDate, int flushDelay, int rewardPack) throws Exception;
  }

  public Disposable gameShowOpenTask;
  public Disposable gameShowCloseTask;
  public Disposable newDayTask;
  public long       gateWayPingTaskId;
  public long       itemStatsSyncTask;

  public long       rankingSyncTaskId;
  public Scheduler  scheduler;
  public Vertx      vertx;

  private static TaskRunner instance = new TaskRunner();
  private TaskRunner() { }
  public static TaskRunner getInstance() { return instance; }

  public void setVXInstance(Vertx vertx) {
    this.vertx = vertx;
  }

  public void scheduleMainTask() {
    this.scheduler    = RxHelper.scheduler(vertx);

    /*Node to gateway heartbeat*/
    gateWayPingTaskId = vertx.setPeriodic(Constant.SYSTEM_INFO.GATEWAY_NOTIFY_INTERVAL, id -> {
      try {
        JsonObject jsonMessage = new JsonObject().put("cmd", "ping");
        jsonMessage.put("cmd", "ping");
        jsonMessage.put("nodeId",       nodeId);
        jsonMessage.put("nodeIp",       nodeIp);
        jsonMessage.put("nodePort",     nodePort);
        jsonMessage.put("nodeName",     nodeName);
        jsonMessage.put("nodeBus",      nodeBus);
        jsonMessage.put("nodeCcu",      SessionPool.getCCU());
        jsonMessage.put("onlineGroup",  GroupPool.getTotal());
        EventBus eb = vertx.eventBus();
        eb.send(Constant.SYSTEM_INFO.GATEWAY_EVT_BUS, jsonMessage);
      }
      catch (Exception e) {
        LOG.globalException("node", "schedule tasks", e);
      }
    });

    itemStatsSyncTask = vertx.setPeriodic(Constant.SYSTEM_INFO.STATS_DATA_SYNC_INTERVAL, id -> {
      try {
        UserInventory.syncItemStatToDB();
      }
      catch (Exception e) {
        LOG.globalException("node", "Sync Stats to DB", e);
      }
    });

    rankingSyncTaskId = vertx.setPeriodic(60*1000, id -> {
      EventLoop.Command closeCommand = new TimeCheckCommand<>(UserRanking.rankings, Constant.RANK_EVENT.evtMap, System.currentTimeMillis());
      UserRanking.rankingEventLoop.addCommand(closeCommand);
    });

    /*NEW DAY TASK*/
    newDayTask = CronObservable.cronspec(scheduler, "0 0 0 * * ? *", TIME_ZONE)
            .subscribe(
              timed -> {
                SessionPool.dailyReset.run();
                LOG.console("execute new day task");
              },
              fault -> LOG.globalException("node","schedule new day task", "error new day task")
            );

    /*GAME SHOW OPEN TASK*/
    String gameShowOpenCron = String.format("%d %d %d,%d * * ? *",
            Constant.SCHEDULE.gameShowOneOpenSec,
            Constant.SCHEDULE.gameShowOneOpenMin,
            Constant.SCHEDULE.gameShowOneOpenHour,
            Constant.SCHEDULE.gameShowTwoOpenHour);
    gameShowOpenTask = CronObservable.cronspec(scheduler, gameShowOpenCron, TIME_ZONE)
            .subscribe(
              timed -> {
                Constant.SCHEDULE.gameShowOpen = true;
                SessionPool.resetGameShowIdols.run();
                LOG.console("open game show");
              },
              fault -> LOG.globalException("node","gameShowOpenTask", fault)
            );

    /*GAME SHOW CLOSE TASK*/
    String gameShowCloseCron = String.format("%d %d %d,%d * * ? *",
            Constant.SCHEDULE.gameShowOneCloseSec,
            Constant.SCHEDULE.gameShowOneCloseMin,
            Constant.SCHEDULE.gameShowOneCloseHour,
            Constant.SCHEDULE.gameShowTwoCloseHour);
    gameShowCloseTask = CronObservable.cronspec(scheduler, gameShowCloseCron, TIME_ZONE)
            .subscribe(
              timed -> {
                Constant.SCHEDULE.gameShowOpen = false;
                SessionPool.resetGameShowIdols.run();
                LOG.console("close game show");
              },
              fault -> LOG.globalException("node","gameShowCloseTask",fault)
            );
  }
}