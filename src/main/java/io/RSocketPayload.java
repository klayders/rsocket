package io;

import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;

public class RSocketPayload implements Payload {

  @Override
  public boolean hasMetadata() {
    return false;
  }

  @Override
  public ByteBuf sliceMetadata() {
    return null;
  }

  @Override
  public ByteBuf sliceData() {
    return null;
  }

  @Override
  public ByteBuf data() {
    return null;
  }

  @Override
  public ByteBuf metadata() {
    return null;
  }

  @Override
  public int refCnt() {
    return 0;
  }

  @Override
  public Payload retain() {
    return null;
  }

  @Override
  public Payload retain(int increment) {
    return null;
  }

  @Override
  public Payload touch() {
    return null;
  }

  @Override
  public Payload touch(Object hint) {
    return null;
  }

  @Override
  public boolean release() {
    return false;
  }

  @Override
  public boolean release(int i) {
    return false;
  }
}
