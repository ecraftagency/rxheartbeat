package eventLoop;

import com.common.GlobalVariable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class EventLoop implements Runnable{
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
        //
      }
    }
    GlobalVariable.schThreadPool.schedule(this, 1000, TimeUnit.MILLISECONDS);
  }

  public interface Command {
    void execute();
  }

  public void addCommand(Command command) {
    incomingCommands.add(command);
  }

  private ConcurrentLinkedQueue<Command> incomingCommands;
}