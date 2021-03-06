package client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketConnector;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static io.rsocket.SocketAcceptor.forRequestStream;
import static java.lang.Thread.sleep;

@Slf4j
public class StreamClient {


  public static void main(String[] args) throws InterruptedException {
    RSocket rsocket =
      RSocketConnector.create()
        .connect(TcpClientTransport.create("localhost", 8765))
        .block();

    assert rsocket != null;

    rsocket
      .requestStream(DefaultPayload.create("Hello"))
      .map(Payload::getDataUtf8)
      .doOnNext(log::info)
      .then()
      .doFinally(signalType -> rsocket.dispose())
      .then()
      .block();

//    rsocket.onClose().block();
    Thread.sleep(1000000);
//    var bean =
//      RSocketConnector.create()
//        .keepAlive(Duration.ofSeconds(5000), Duration.ofSeconds(5000))
//        // Enable Zero Copy
//        .payloadDecoder(PayloadDecoder.ZERO_COPY)
//        .connect(TcpClientTransport.create("localhost", 8765));
//
//    final RSocket block = bean.block();
//
//
//    // готово
//    Flux.range(0, 10)
//      .flatMap(integer -> send(block, "m" + integer))
////      .repeatWhen(r -> r.delayElements(Duration.ofSeconds(5)))
//      .onErrorContinue((e, o) -> log.error("obj=" + o, e))
//      .publishOn(Schedulers.newParallel("pp", 5))
//      .subscribeOn(Schedulers.newParallel("ss", 5))
//      .subscribe();

    sleep(500_000);


  }

  // process messages in one tcp connection
  private static Flux<String> send(RSocket rSocket, String message) {
    return rSocket.requestStream(DefaultPayload.create(message))
      .limitRate(10)
      .map(result -> {

          var responseData = result.getDataUtf8();
          result.release();
          log.info("request={}, response={}", message, responseData);
          return responseData;
        }
      )
      .subscribeOn(Schedulers.newParallel("stream-client", 5));
  }

}
