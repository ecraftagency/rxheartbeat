package com.heartbeat.ranking;

import com.common.GlobalVariable;
import com.common.LOG;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import static com.common.Constant.*;

/*simple event loop implement in java[/-script-/]*/
public class EventLoop implements Runnable{
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
        LOG.globalException("node", "eventLoop", e);
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