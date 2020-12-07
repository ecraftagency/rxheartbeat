import com.heartbeat.effect.EffectHandler;
import com.heartbeat.effect.EffectManager;
import com.heartbeat.model.Session;
import com.heartbeat.model.data.UserGameInfo;
import com.heartbeat.model.data.UserIdol;
import com.heartbeat.model.data.UserInventory;

import java.util.Arrays;

public class EffectTest {
  public static void main(String[] args) {
    Session session = Session.of(1000000);
    session.userGameInfo = UserGameInfo.ofDefault();
    session.userInventory = UserInventory.ofDefault();
    session.userIdol = UserIdol.ofDefault();

    System.out.println("money before: " + session.userGameInfo.money);
    EffectManager.inst().handleEffect(EffectHandler.ExtArgs.of(), session, Arrays.asList(1,1,100,0));
    System.out.println("money after: " + session.userGameInfo.money);
  }
}
