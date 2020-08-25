package eventLoop;

import com.common.GlobalVariable;
import eventLoop.commands.GetLDBCommand;
import eventLoop.commands.RecordCommand;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
  public static class RecordThread extends Thread {
    private EventLoop evt;
    private LDB ldb;
    private final int randBound;
    public RecordThread(EventLoop evt, LDB ldb, int randomBound) {
      this.evt = evt;
      this.ldb = ldb;
      this.randBound = randomBound;
    }
    @Override
    public void run() {
      try {
        while (true) {
          ScoreObject so = ScoreObject.of(ThreadLocalRandom.current().nextInt(2000), ThreadLocalRandom.current().nextInt(randBound), "hello");
          evt.addCommand(new RecordCommand(ldb, so));
          Thread.sleep(10);
        }
      }
      catch (Exception e) {
        //
      }
    }
  }
  public static void main(String[] args) throws InterruptedException {
    LDB ldb = new LDB(10);
    EventLoop evt = new EventLoop();
    //GlobalVariable.exeThreadPool.execute(evt);

    RecordThread record1 = new RecordThread(evt, ldb, 20000);
    RecordThread record2 = new RecordThread(evt, ldb, 30000);
    RecordThread record3 = new RecordThread(evt, ldb, 40000);
    RecordThread record4 = new RecordThread(evt, ldb, 50000);
    RecordThread record5 = new RecordThread(evt, ldb, 60000);
    record1.start();
    record2.start();
    record3.start();
    record4.start();
    record5.start();

    Thread get1 = new Thread(() -> {
      try {
        while (true) {
          EventLoop.Command command = new GetLDBCommand(ldb, ar -> {
            if (ar.succeeded()) {
              List<ScoreObject> res = ar.result();
              for (ScoreObject so : res)
                System.out.println(so);
              System.out.println();
            }
          });
          evt.addCommand(command);
          Thread.sleep(1000);
        }
      }
      catch (Exception e) {
        //
      }
    });
    get1.start();
  }
}
