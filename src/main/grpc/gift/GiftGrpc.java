package gift;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.20.0)",
    comments = "Source: gift.proto")
public final class GiftGrpc {

  private GiftGrpc() {}

  private static <T> io.grpc.stub.StreamObserver<T> toObserver(final io.vertx.core.Handler<io.vertx.core.AsyncResult<T>> handler) {
    return new io.grpc.stub.StreamObserver<T>() {
      private volatile boolean resolved = false;
      @Override
      public void onNext(T value) {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.succeededFuture(value));
        }
      }

      @Override
      public void onError(Throwable t) {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.failedFuture(t));
        }
      }

      @Override
      public void onCompleted() {
        if (!resolved) {
          resolved = true;
          handler.handle(io.vertx.core.Future.succeededFuture());
        }
      }
    };
  }

  public static final String SERVICE_NAME = "gift.Gift";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<gift.HelloRequest,
      gift.HelloReply> getSayHelloMethod;

  public static io.grpc.MethodDescriptor<gift.HelloRequest,
      gift.HelloReply> getSayHelloMethod() {
    io.grpc.MethodDescriptor<gift.HelloRequest, gift.HelloReply> getSayHelloMethod;
    if ((getSayHelloMethod = GiftGrpc.getSayHelloMethod) == null) {
      synchronized (GiftGrpc.class) {
        if ((getSayHelloMethod = GiftGrpc.getSayHelloMethod) == null) {
          GiftGrpc.getSayHelloMethod = getSayHelloMethod = 
              io.grpc.MethodDescriptor.<gift.HelloRequest, gift.HelloReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "gift.Gift", "SayHello"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  gift.HelloRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  gift.HelloReply.getDefaultInstance()))
                  .setSchemaDescriptor(new GiftMethodDescriptorSupplier("SayHello"))
                  .build();
          }
        }
     }
     return getSayHelloMethod;
  }

  private static volatile io.grpc.MethodDescriptor<gift.ClaimGiftRequest,
      gift.ClaimGiftResponse> getClaimGiftMethod;

  public static io.grpc.MethodDescriptor<gift.ClaimGiftRequest,
      gift.ClaimGiftResponse> getClaimGiftMethod() {
    io.grpc.MethodDescriptor<gift.ClaimGiftRequest, gift.ClaimGiftResponse> getClaimGiftMethod;
    if ((getClaimGiftMethod = GiftGrpc.getClaimGiftMethod) == null) {
      synchronized (GiftGrpc.class) {
        if ((getClaimGiftMethod = GiftGrpc.getClaimGiftMethod) == null) {
          GiftGrpc.getClaimGiftMethod = getClaimGiftMethod = 
              io.grpc.MethodDescriptor.<gift.ClaimGiftRequest, gift.ClaimGiftResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "gift.Gift", "ClaimGift"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  gift.ClaimGiftRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  gift.ClaimGiftResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new GiftMethodDescriptorSupplier("ClaimGift"))
                  .build();
          }
        }
     }
     return getClaimGiftMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GiftStub newStub(io.grpc.Channel channel) {
    return new GiftStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GiftBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GiftBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GiftFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GiftFutureStub(channel);
  }

  /**
   * Creates a new vertx stub that supports all call types for the service
   */
  public static GiftVertxStub newVertxStub(io.grpc.Channel channel) {
    return new GiftVertxStub(channel);
  }

  /**
   */
  public static abstract class GiftImplBase implements io.grpc.BindableService {

    /**
     */
    public void sayHello(gift.HelloRequest request,
        io.grpc.stub.StreamObserver<gift.HelloReply> responseObserver) {
      asyncUnimplementedUnaryCall(getSayHelloMethod(), responseObserver);
    }

    /**
     */
    public void claimGift(gift.ClaimGiftRequest request,
        io.grpc.stub.StreamObserver<gift.ClaimGiftResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getClaimGiftMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSayHelloMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                gift.HelloRequest,
                gift.HelloReply>(
                  this, METHODID_SAY_HELLO)))
          .addMethod(
            getClaimGiftMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                gift.ClaimGiftRequest,
                gift.ClaimGiftResponse>(
                  this, METHODID_CLAIM_GIFT)))
          .build();
    }
  }

  /**
   */
  public static final class GiftStub extends io.grpc.stub.AbstractStub<GiftStub> {
    public GiftStub(io.grpc.Channel channel) {
      super(channel);
    }

    public GiftStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GiftStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GiftStub(channel, callOptions);
    }

    /**
     */
    public void sayHello(gift.HelloRequest request,
        io.grpc.stub.StreamObserver<gift.HelloReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSayHelloMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void claimGift(gift.ClaimGiftRequest request,
        io.grpc.stub.StreamObserver<gift.ClaimGiftResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getClaimGiftMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GiftBlockingStub extends io.grpc.stub.AbstractStub<GiftBlockingStub> {
    public GiftBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    public GiftBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GiftBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GiftBlockingStub(channel, callOptions);
    }

    /**
     */
    public gift.HelloReply sayHello(gift.HelloRequest request) {
      return blockingUnaryCall(
          getChannel(), getSayHelloMethod(), getCallOptions(), request);
    }

    /**
     */
    public gift.ClaimGiftResponse claimGift(gift.ClaimGiftRequest request) {
      return blockingUnaryCall(
          getChannel(), getClaimGiftMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GiftFutureStub extends io.grpc.stub.AbstractStub<GiftFutureStub> {
    public GiftFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    public GiftFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GiftFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GiftFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<gift.HelloReply> sayHello(
        gift.HelloRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSayHelloMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<gift.ClaimGiftResponse> claimGift(
        gift.ClaimGiftRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getClaimGiftMethod(), getCallOptions()), request);
    }
  }

  /**
   */
  public static abstract class GiftVertxImplBase implements io.grpc.BindableService {

    /**
     */
    public void sayHello(gift.HelloRequest request,
        io.vertx.core.Promise<gift.HelloReply> response) {
      asyncUnimplementedUnaryCall(getSayHelloMethod(), GiftGrpc.toObserver(response));
    }

    /**
     */
    public void claimGift(gift.ClaimGiftRequest request,
        io.vertx.core.Promise<gift.ClaimGiftResponse> response) {
      asyncUnimplementedUnaryCall(getClaimGiftMethod(), GiftGrpc.toObserver(response));
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSayHelloMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                gift.HelloRequest,
                gift.HelloReply>(
                  this, METHODID_SAY_HELLO)))
          .addMethod(
            getClaimGiftMethod(),
            asyncUnaryCall(
              new VertxMethodHandlers<
                gift.ClaimGiftRequest,
                gift.ClaimGiftResponse>(
                  this, METHODID_CLAIM_GIFT)))
          .build();
    }
  }

  /**
   */
  public static final class GiftVertxStub extends io.grpc.stub.AbstractStub<GiftVertxStub> {
    public GiftVertxStub(io.grpc.Channel channel) {
      super(channel);
    }

    public GiftVertxStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GiftVertxStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GiftVertxStub(channel, callOptions);
    }

    /**
     */
    public void sayHello(gift.HelloRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<gift.HelloReply>> response) {
      asyncUnaryCall(
          getChannel().newCall(getSayHelloMethod(), getCallOptions()), request, GiftGrpc.toObserver(response));
    }

    /**
     */
    public void claimGift(gift.ClaimGiftRequest request,
        io.vertx.core.Handler<io.vertx.core.AsyncResult<gift.ClaimGiftResponse>> response) {
      asyncUnaryCall(
          getChannel().newCall(getClaimGiftMethod(), getCallOptions()), request, GiftGrpc.toObserver(response));
    }
  }

  private static final int METHODID_SAY_HELLO = 0;
  private static final int METHODID_CLAIM_GIFT = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GiftImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GiftImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SAY_HELLO:
          serviceImpl.sayHello((gift.HelloRequest) request,
              (io.grpc.stub.StreamObserver<gift.HelloReply>) responseObserver);
          break;
        case METHODID_CLAIM_GIFT:
          serviceImpl.claimGift((gift.ClaimGiftRequest) request,
              (io.grpc.stub.StreamObserver<gift.ClaimGiftResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class VertxMethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GiftVertxImplBase serviceImpl;
    private final int methodId;

    VertxMethodHandlers(GiftVertxImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SAY_HELLO:
          serviceImpl.sayHello((gift.HelloRequest) request,
              (io.vertx.core.Promise<gift.HelloReply>) io.vertx.core.Promise.<gift.HelloReply>promise().future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<gift.HelloReply>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
          break;
        case METHODID_CLAIM_GIFT:
          serviceImpl.claimGift((gift.ClaimGiftRequest) request,
              (io.vertx.core.Promise<gift.ClaimGiftResponse>) io.vertx.core.Promise.<gift.ClaimGiftResponse>promise().future().setHandler(ar -> {
                if (ar.succeeded()) {
                  ((io.grpc.stub.StreamObserver<gift.ClaimGiftResponse>) responseObserver).onNext(ar.result());
                  responseObserver.onCompleted();
                } else {
                  responseObserver.onError(ar.cause());
                }
              }));
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class GiftBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GiftBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return gift.GiftService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Gift");
    }
  }

  private static final class GiftFileDescriptorSupplier
      extends GiftBaseDescriptorSupplier {
    GiftFileDescriptorSupplier() {}
  }

  private static final class GiftMethodDescriptorSupplier
      extends GiftBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GiftMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GiftGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GiftFileDescriptorSupplier())
              .addMethod(getSayHelloMethod())
              .addMethod(getClaimGiftMethod())
              .build();
        }
      }
    }
    return result;
  }
}
