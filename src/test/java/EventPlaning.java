import com.heartbeat.event.Common;
import com.heartbeat.event.TimingEvent;

public class EventPlaning {
  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    String res = Common.updatePlan(TimingEvent.evtPlan,
            "id,eventId,startDate,endDate,flushDelay,rewardPack\n" +
            "1,21,26/11/2020 16:50:00,26/11/2020 16:56:00,300,1\n" +
            "2,21,26/11/2020 17:50:00,26/11/2020 17:56:00,300,1\n");

    long end = System.currentTimeMillis();

    System.out.println(res);
    System.out.println(end - start);
  }
}