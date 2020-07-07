import com.heartbeat.model.data.UserIdol;
import com.heartbeat.model.data.UserInventory;
import com.transport.ExtMessage;

import java.lang.reflect.Field;

public class SnapShot {
  public static void main(String[] args) throws InstantiationException, IllegalAccessException {
    ExtMessage local = ExtMessage.profile();
    local.data.idols = UserIdol.ofDefault();

    ExtMessage remote = ExtMessage.production();
    remote.data.inventory = UserInventory.ofDefault();

    merge(local, remote);
    System.out.println(local);
  }

  public static <T> T merge(T local, T remote) throws IllegalAccessException, InstantiationException {
    Class<?> clazz = local.getClass();
    Object merged = clazz.newInstance();
    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      Object localValue = field.get(local);
      Object remoteValue = field.get(remote);
      if (localValue != null) {
        switch (localValue.getClass().getSimpleName()) {
          case "Default":
          case "Detail":
            field.set(merged, merge(localValue, remoteValue));
            break;
          default:
            field.set(merged, (remoteValue != null) ? remoteValue : localValue);
        }
      }
    }
    return (T) merged;
  }
}
