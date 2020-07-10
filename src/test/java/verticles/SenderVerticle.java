package verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class SenderVerticle extends AbstractVerticle {
  private EventBus  eventBus;

  private void assignEventBus() {
    eventBus = vertx.eventBus();
  }

  @Override
  public void start() {
    assignEventBus();

    vertx.createHttpServer().requestHandler(res ->{
      JsonObject jsonMessage = new JsonObject().put("message_from_sender_verticle", "hello consumer");
      System.out.println();
      eventBus.request("Consumer", jsonMessage, ar -> {
        if(ar.succeeded()) {
          SenderLauncher.mgr.<String,String>getAsyncMap("sharemap", ress -> {
            if (ress.succeeded()) {
              ress.result().get("hello", arr -> {
                res.response().end(arr.result());
              });
            }
          });
        }
      });
    }).listen(8080);
  }
}
