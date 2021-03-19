package io;

import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

import java.time.Duration;


public class RSocketPayload implements Payload {

  public static void main(String[] args) throws InterruptedException {

    int requestSize = 2;

    var result = findAll(requestSize)
      .doOnNext(System.out::println)
      .collectList()
      .block();

    System.out.println("result size=" + result.size());


//  Flux<Integer> fileFlux = Flux.push(sink -> {
//    watcher.onError(err -> sink.error(err));
//    watcher.onNewFile(file -> sink.next(file));
//  });
//
//    fileFlux
//      .onBackPressureBuffer(10,BufferOverflowStrategy.DROP_LATEST)
//    .


  }

  private static Flux<Long> findAll(int requestSize) {
    return Flux.interval(Duration.ofMillis(1000))
      .take(10)
      .onBackpressureBuffer(requestSize, BufferOverflowStrategy.DROP_OLDEST);


//    return  Flux.interval(ofMillis(100))
//      .take(requestSize)
//      .onBackpressureBuffer(requestSize);

//    return Flux.range(0, 100)
//      .doOnNext(System.out::println)
  }


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
