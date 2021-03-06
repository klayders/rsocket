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
public class FAFClient {


  public static void main(String[] args) throws InterruptedException {
    var bean =
      RSocketConnector.create()
        // Enable Zero Copy
        .keepAlive(Duration.ofSeconds(500), Duration.ofSeconds(500))
        .payloadDecoder(PayloadDecoder.ZERO_COPY)
        .connect(TcpClientTransport.create("localhost", 8765));

    final RSocket block = bean.block();


    Flux.range(0, 10)
      .flatMap(integer -> send(block, "m" + integer))
      .repeatWhen(r -> r.delayElements(Duration.ofSeconds(5)))
      .onErrorContinue((e, o) -> log.error("obj=" + o, e))
      .publishOn(Schedulers.newParallel("pp", 5))
      .subscribeOn(Schedulers.newParallel("ss", 5))
      .subscribe();

    sleep(500_000);


  }


  // process messages in one tcp connection
  private static Mono<Void> send(RSocket rSocket, String message) {
    return rSocket.fireAndForget(DefaultPayload.create(message))
      .subscribeOn(Schedulers.newParallel("faf-client", 5));
  }

}
