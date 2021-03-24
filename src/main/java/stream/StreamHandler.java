package stream;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
public class StreamHandler {

  protected static Mono<RSocket> fromClientRequestStream(RSocket rsocket, String serverName) {

    rsocket
      .requestStream(DefaultPayload.create("Hello-Bidi"))
      .map(Payload::getDataUtf8)
      .log()
      .subscribe();

    return Mono.just(new RSocket() {
    });
  }

  protected static SocketAcceptor requestStream(String serverName) {
    return SocketAcceptor.forRequestStream(
      payload ->
        Flux.interval(Duration.ofMillis(100))
          .map(aLong -> {

            var data = "server=" + serverName + " Interval: " + aLong;
            log.info("send: data={}", data);
            return DefaultPayload.create(data);
          })
    );
  }


//    return SocketAcceptor.forRequestStream(payload -> {
//
//        return Flux.defer(() -> {
//            return Flux.range(1, 20)
//              .doOnRequest(value -> log.info("Received Request For: {}", value))
//              .map(i -> {
//                log.info("Sending: {}", i);
//                return DefaultPayload.create(serverName + " count=" + i);
//              });
//          }
//        )
//          .subscribeOn(Schedulers.newParallel("request-stream-parallel", 10));
//      }
//    );
}

