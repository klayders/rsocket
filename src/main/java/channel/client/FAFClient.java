package channel.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
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
    var rsocket =
      RSocketConnector.create()
        .connect(TcpClientTransport.create("localhost", 8765))
        .block();

    assert rsocket != null;

    Flux.range(0, 10)
      .flatMap(integer -> fireAndForget(rsocket, "m" + integer))
      .repeatWhen(r -> r.delayElements(Duration.ofSeconds(5)))
      .onErrorContinue((e, o) -> log.error("obj=" + o, e))
      .publishOn(Schedulers.newParallel("pp", 5))
      .subscribeOn(Schedulers.newParallel("ss", 5))
      .subscribe();

    sleep(500_000);

  }

  // process messages in one tcp connection
  private static Mono<Void> fireAndForget(RSocket rSocket, String message) {
    log.info("fireAndForget: send={}", message);
    return rSocket.fireAndForget(DefaultPayload.create(message))
      .subscribeOn(Schedulers.newParallel("faf-client", 5));
  }

}
