package com.heartbeat.vertices;

import com.diabolicallabs.vertx.cron.CronObservable;
import com.heartbeat.common.Constant;
import com.heartbeat.model.SessionPool;
import io.reactivex.Scheduler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.reactivex.RxHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameNode extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(GameNode.class);

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    Scheduler scheduler = RxHelper.scheduler(vertx);
    CronObservable.cronspec(scheduler, "0 0 0 * * ? *", "Asia/Ho_Chi_Minh")
            .subscribe(
                    timed -> {
                      SessionPool.resetUserFight.run();
                      LOGGER.info("execute new day task");
                    },
                    fault -> LOGGER.error("error new day task")
            );

    CronObservable.cronspec(scheduler, "0 0 12,19 * * ? *", "Asia/Ho_Chi_Minh")
            .subscribe(
                    timed -> {
                      Constant.SCHEDULE.gameShowOpen = true;
                      SessionPool.resetGameShowIdols.run();
                      LOGGER.info("open game show");
                    },
                    fault -> LOGGER.error("error open game show task")
            );

    CronObservable.cronspec(scheduler, "0 0 14,21 * * ? *", "Asia/Ho_Chi_Minh")
            .subscribe(
                    timed -> {
                      Constant.SCHEDULE.gameShowOpen = false;
                      SessionPool.resetGameShowIdols.run();
                      LOGGER.info("close game show");
                    },
                    fault -> LOGGER.error("error close game show task")
            );
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {

  }
}
