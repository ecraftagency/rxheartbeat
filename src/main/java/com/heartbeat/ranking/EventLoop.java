package com.heartbeat.ranking;

import com.common.GlobalVariable;
import com.heartbeat.controller.AchievementController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import static com.common.Constant.*;

/*simple event loop implement in java[/-script-/]*/
public class EventLoop implements Runnable{
  private static final Logger LOGGER = LoggerFactory.getLogger(AchievementController.class);
  private ConcurrentLinkedQueue<Command> incomingCommands;

  public EventLoop() {
    incomingCommands = new ConcurrentLinkedQueue<>();
  }

  @Override
  public void run() {
    while (!incomingCommands.isEmpty()) {
      try {
        Command command = incomingCommands.poll();
        if (command != null)
          command.execute();
      }
      catch (Exception e) {
        LOGGER.error(e.getMessage());
      }
    }
    GlobalVariable.schThreadPool.schedule(this, SYSTEM_INFO.EVENT_LOOP_SLEEP_INV, TimeUnit.MILLISECONDS);
  }

  @FunctionalInterface
  public interface Command {
    void execute();
  }

  public void addCommand(Command command) {
    incomingCommands.add(command);
  }
}