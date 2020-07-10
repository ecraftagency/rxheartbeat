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
  public void start() throws Exception {
    assignEventBus();
    registerHandler();
    SharedData sharedData = vertx.sharedData();
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
