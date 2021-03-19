package client;


import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static java.lang.Thread.sleep;

@Slf4j
public class RARClient {


  public static void main(String[] args) throws InterruptedException {
    var bean =
      RSocketConnector.create()
        .keepAlive(Duration.ofSeconds(5000), Duration.ofSeconds(5000))
        // Enable Zero Copy
        .payloadDecoder(PayloadDecoder.ZERO_COPY)
        .connect(TcpClientTransport.create("localhost", 8765));

    Flux.range(0, 10)
      .flatMap(integer -> startSendingMessage1(bean, integer))
      .repeatWhen(r -> r.delayElements(Duration.ofSeconds(5)))
      .onErrorContinue((e, o) -> log.error("obj=" + o, e))
      .publishOn(Schedulers.newParallel("pp", 5))
      .subscribeOn(Schedulers.newParallel("ss", 5))
      .subscribe();

//    final RSocket block = bean.block();
//
//
//    // готово
//    Flux.range(0, 10)
//      .flatMap(integer -> send(block, "m" + integer))
//      .repeatWhen(r -> r.delayElements(Duration.ofSeconds(5)))
//      .onErrorContinue((e, o) -> log.error("obj=" + o, e))
//      .publishOn(Schedulers.newParallel("pp", 5))
//      .subscribeOn(Schedulers.newParallel("ss", 5))
//      .subscribe();

    sleep(500_000);


  }

  // create multiple connection for send message
  private static Mono<String> startSendingMessage1(Mono<RSocket> bean, Integer integer) {
    return bean.flatMap(
      rSocket -> send(rSocket, "m" + integer).flatMap(response -> send(rSocket, response)).flatMap(response -> send(rSocket, response))
    );
  }

  // create multiple connection for send message
  private static Mono<String> startSendingMessage(Mono<RSocket> bean, Integer integer) {
    return bean.flatMap(
      rSocket -> send(rSocket, "m" + integer).flatMap(response -> send(rSocket, response)).flatMap(response -> send(rSocket, response))
    );
  }

  // process messages in one tcp connection
  private static Mono<String> send(RSocket rSocket, String message) {
    return rSocket.requestResponse(DefaultPayload.create(message))
      .map(result -> {

          var responseData = result.getDataUtf8();
          result.release();
          log.info("request={}, response={}", message, responseData);
          return responseData;
        }
      )
      .publishOn(Schedulers.newParallel("pp", 5))
      .subscribeOn(Schedulers.newParallel("ss", 5));
  }

}
