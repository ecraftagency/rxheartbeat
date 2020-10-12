package verticles;

import gift.ClaimGiftRequest;
import gift.HelloRequest;
import grpc.gift.GiftGrpc;
import io.grpc.ManagedChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.grpc.VertxChannelBuilder;

public class SenderVerticle extends AbstractVerticle {
  private EventBus  eventBus;
  GiftGrpc.GiftVertxStub stub;
  private void assignEventBus() {
    eventBus = vertx.eventBus();
  }


  private void claimGift() {
    ClaimGiftRequest request = ClaimGiftRequest.newBuilder().setGiftCode("GHSIABBAAS").setServerId(1).setUserId(1000000).build();
    stub.claimGift(request, ar -> {
      if (ar.succeeded()) {
        System.out.println("Got the server response: " + ar.result().getRewardFormat());
      } else {
        System.out.println("Coult not reach server " + ar.cause().getMessage());
      }
    });
  }

  @Override
  public void start() {
    assignEventBus();

    ManagedChannel channel = VertxChannelBuilder
            .forAddress(vertx, "localhost", 9000)
            .usePlaintext(true)
            .build();

// Get a stub to use for interacting with the remote service
    stub = GiftGrpc.newVertxStub(channel);

    claimGift();
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
    }).listen(3000);
  }
}

/*
//      mgr.<Integer, Node>getAsyncMap("nodePool", ar -> {
//        if (ar.succeeded()) {
//          AsyncMap<Integer, Node> nodePool = ar.result();
//          Node node     = Node.of();
//          node.ccu      = SessionPool.getCCU();
//          node.nodeIp   = nodeIp;
//          node.nodePort = nodePort;
//          node.nodeId   = nodeId;
//          nodePool.put(nodeId, node, sar -> {
//            if (sar.failed()) {
//              LOGGER.error(String.format("Fail to sync node info, nodeId %d, %s", nodeId, sar.cause()));
//            }
//          });
//        }
//        else {
//          LOGGER.error(String.format("Fail to load nodePool, nodeId: %d", nodeId));
//        }
//      });
 */
