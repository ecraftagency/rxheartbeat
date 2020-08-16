package verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;

public class ConsumeVerticle extends AbstractVerticle {
  private EventBus eventBus;

  @Override
  public void start() {
    assignEventBus();
    registerHandler();
  }

  private void assignEventBus() {
    eventBus = vertx.eventBus();
  }

  private void registerHandler() {
    String verticleAddress = "Consumer";
    MessageConsumer<JsonObject> messageConsumer = eventBus.consumer(verticleAddress);
    messageConsumer.handler(message -> {
      ConsumeLauncher.mgr.<String,String>getAsyncMap("sharemap", res -> {
        JsonObject jsonMessage = message.body();
        System.out.println(jsonMessage.getValue("message_from_sender_verticle"));

        if (res.succeeded()) {
          AsyncMap<String, String> map = res.result();
          map.put("hello", System.currentTimeMillis() + "", ar -> {
            if (ar.succeeded()) {
              JsonObject jsonReply = new JsonObject().put("reply", ConsumeLauncher.mgr.getNodes());
              message.reply(jsonReply);
            }
          });
        }
      });
    });
  }
}

/*
    syncMapTaskID = vertx.setPeriodic(Constant.SYSTEM_INFO.GATEWAY_NOTIFY_INTERVAL, id -> {
      mgr.<Integer,Node>getAsyncMap("nodePool", ar -> {
        if (ar.succeeded()) {
          AsyncMap<Integer, Node> nodePool = ar.result();
          nodePool.entries(ear -> {
            if (ear.succeeded()) {
              Map<Integer, Node> pool = ear.result();
              for (Node node : pool.values())
                System.out.println(node.nodeIp);
            }
            else {
              LOGGER.error("error fetching entries from NodePool");
            }
          });
        }
        else {
          LOGGER.error("error fetching NodePool");
        }
      });
    });
 */
