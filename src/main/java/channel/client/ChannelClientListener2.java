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

import static java.lang.Thread.sleep;

@Slf4j
public class ChannelClientListener2 {


  public static void main(String[] args) throws InterruptedException {
    var rsocket =
      RSocketConnector.create()
        .connect(TcpClientTransport.create("localhost", 8765))
        .block();

    assert rsocket != null;

    rsocket
      .requestChannel(
        Flux.range(0, 5)
          .map(integer -> DefaultPayload.create(" inner_count=" + integer))
      )
      .map(Payload::getDataUtf8)
      .doOnNext(s -> log.info("requestChannel: receive={}", s))
      .doFinally(signalType -> rsocket.dispose())
      .then()
      .subscribe();

    sleep(500_000);
  }


}
