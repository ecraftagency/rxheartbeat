// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: gift.proto

package gift;

/**
 * Protobuf type {@code gift.ClaimGiftRequest}
 */
public  final class ClaimGiftRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:gift.ClaimGiftRequest)
    ClaimGiftRequestOrBuilder {
  // Use ClaimGiftRequest.newBuilder() to construct.
  private ClaimGiftRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ClaimGiftRequest() {
    serverId_ = 0;
    userId_ = 0;
    giftCode_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private ClaimGiftRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 8: {

            serverId_ = input.readInt32();
            break;
          }
          case 16: {

            userId_ = input.readInt32();
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();

            giftCode_ = s;
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return gift.GiftService.internal_static_gift_ClaimGiftRequest_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return gift.GiftService.internal_static_gift_ClaimGiftRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            gift.ClaimGiftRequest.class, gift.ClaimGiftRequest.Builder.class);
  }

  public static final int SERVERID_FIELD_NUMBER = 1;
  private int serverId_;
  /**
   * <code>int32 serverId = 1;</code>
   */
  public int getServerId() {
    return serverId_;
  }

  public static final int USERID_FIELD_NUMBER = 2;
  private int userId_;
  /**
   * <code>int32 userId = 2;</code>
   */
  public int getUserId() {
    return userId_;
  }

  public static final int GIFTCODE_FIELD_NUMBER = 3;
  private volatile java.lang.Object giftCode_;
  /**
   * <code>string giftCode = 3;</code>
   */
  public java.lang.String getGiftCode() {
    java.lang.Object ref = giftCode_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      giftCode_ = s;
      return s;
    }
  }
  /**
   * <code>string giftCode = 3;</code>
   */
  public com.google.protobuf.ByteString
      getGiftCodeBytes() {
    java.lang.Object ref = giftCode_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      giftCode_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (serverId_ != 0) {
      output.writeInt32(1, serverId_);
    }
    if (userId_ != 0) {
      output.writeInt32(2, userId_);
    }
    if (!getGiftCodeBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, giftCode_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (serverId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, serverId_);
    }
    if (userId_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, userId_);
    }
    if (!getGiftCodeBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, giftCode_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof gift.ClaimGiftRequest)) {
      return super.equals(obj);
    }
    gift.ClaimGiftRequest other = (gift.ClaimGiftRequest) obj;

    boolean result = true;
    result = result && (getServerId()
        == other.getServerId());
    result = result && (getUserId()
        == other.getUserId());
    result = result && getGiftCode()
        .equals(other.getGiftCode());
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + SERVERID_FIELD_NUMBER;
    hash = (53 * hash) + getServerId();
    hash = (37 * hash) + USERID_FIELD_NUMBER;
    hash = (53 * hash) + getUserId();
    hash = (37 * hash) + GIFTCODE_FIELD_NUMBER;
    hash = (53 * hash) + getGiftCode().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static gift.ClaimGiftRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static gift.ClaimGiftRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static gift.ClaimGiftRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static gift.ClaimGiftRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static gift.ClaimGiftRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static gift.ClaimGiftRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static gift.ClaimGiftRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static gift.ClaimGiftRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static gift.ClaimGiftRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static gift.ClaimGiftRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(gift.ClaimGiftRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code gift.ClaimGiftRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:gift.ClaimGiftRequest)
      gift.ClaimGiftRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return gift.GiftService.internal_static_gift_ClaimGiftRequest_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return gift.GiftService.internal_static_gift_ClaimGiftRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              gift.ClaimGiftRequest.class, gift.ClaimGiftRequest.Builder.class);
    }

    // Construct using gift.ClaimGiftRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      serverId_ = 0;

      userId_ = 0;

      giftCode_ = "";

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return gift.GiftService.internal_static_gift_ClaimGiftRequest_descriptor;
    }

    public gift.ClaimGiftRequest getDefaultInstanceForType() {
      return gift.ClaimGiftRequest.getDefaultInstance();
    }

    public gift.ClaimGiftRequest build() {
      gift.ClaimGiftRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public gift.ClaimGiftRequest buildPartial() {
      gift.ClaimGiftRequest result = new gift.ClaimGiftRequest(this);
      result.serverId_ = serverId_;
      result.userId_ = userId_;
      result.giftCode_ = giftCode_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof gift.ClaimGiftRequest) {
        return mergeFrom((gift.ClaimGiftRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(gift.ClaimGiftRequest other) {
      if (other == gift.ClaimGiftRequest.getDefaultInstance()) return this;
      if (other.getServerId() != 0) {
        setServerId(other.getServerId());
      }
      if (other.getUserId() != 0) {
        setUserId(other.getUserId());
      }
      if (!other.getGiftCode().isEmpty()) {
        giftCode_ = other.giftCode_;
        onChanged();
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      gift.ClaimGiftRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (gift.ClaimGiftRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int serverId_ ;
    /**
     * <code>int32 serverId = 1;</code>
     */
    public int getServerId() {
      return serverId_;
    }
    /**
     * <code>int32 serverId = 1;</code>
     */
    public Builder setServerId(int value) {
      
      serverId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 serverId = 1;</code>
     */
    public Builder clearServerId() {
      
      serverId_ = 0;
      onChanged();
      return this;
    }

    private int userId_ ;
    /**
     * <code>int32 userId = 2;</code>
     */
    public int getUserId() {
      return userId_;
    }
    /**
     * <code>int32 userId = 2;</code>
     */
    public Builder setUserId(int value) {
      
      userId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 userId = 2;</code>
     */
    public Builder clearUserId() {
      
      userId_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object giftCode_ = "";
    /**
     * <code>string giftCode = 3;</code>
     */
    public java.lang.String getGiftCode() {
      java.lang.Object ref = giftCode_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        giftCode_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string giftCode = 3;</code>
     */
    public com.google.protobuf.ByteString
        getGiftCodeBytes() {
      java.lang.Object ref = giftCode_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        giftCode_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string giftCode = 3;</code>
     */
    public Builder setGiftCode(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      giftCode_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string giftCode = 3;</code>
     */
    public Builder clearGiftCode() {
      
      giftCode_ = getDefaultInstance().getGiftCode();
      onChanged();
      return this;
    }
    /**
     * <code>string giftCode = 3;</code>
     */
    public Builder setGiftCodeBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      giftCode_ = value;
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:gift.ClaimGiftRequest)
  }

  // @@protoc_insertion_point(class_scope:gift.ClaimGiftRequest)
  private static final gift.ClaimGiftRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new gift.ClaimGiftRequest();
  }

  public static gift.ClaimGiftRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ClaimGiftRequest>
      PARSER = new com.google.protobuf.AbstractParser<ClaimGiftRequest>() {
    public ClaimGiftRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new ClaimGiftRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ClaimGiftRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ClaimGiftRequest> getParserForType() {
    return PARSER;
  }

  public gift.ClaimGiftRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
