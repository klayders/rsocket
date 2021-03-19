package client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.User;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
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
public class ExampleClient {

  private static final ObjectMapper mapper = new ObjectMapper();


  public static void main(String[] args) throws InterruptedException {

    var user = new User("name");

    var bean =
      RSocketConnector.create()
        // Enable Zero Copy
        .payloadDecoder(PayloadDecoder.ZERO_COPY)
        .connect(TcpClientTransport.create("localhost", 8765));

//    RSocketClient rSocketClient = new DefaultRSocketClient()
    final RSocket block = bean.block();


    // готово
    Flux.range(0, 10)
      .flatMap(integer -> send(block, "m" + integer))
      .repeatWhen(r -> r.delayElements(Duration.ofSeconds(5)))
      .onErrorContinue((e, o) -> log.error("obj=" + o, e))
      .publishOn(Schedulers.newParallel("pp", 5))
      .subscribeOn(Schedulers.newParallel("ss", 5))
      .subscribe();

    sleep(500_000);


  }
//
//  private static Mono<String> startSendingMessage1(RSocket bean, Integer integer) {
//    return
//      rSocket -> send(rSocket, "m" + integer).flatMap(response -> send(rSocket, response)).flatMap(response -> send(rSocket, response))
//  }

  private static Mono<String> startSendingMessage(Mono<RSocket> bean, Integer integer) {
    return bean.flatMap(
      rSocket -> send(rSocket, "m" + integer).flatMap(response -> send(rSocket, response)).flatMap(response -> send(rSocket, response))
    );
  }

  private static Mono<String> send(RSocket rSocket, String message) {
    return rSocket.fireAndForget(DefaultPayload.create(message))
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
//
//  private static Mono<User> startSendingMessage(Mono<RSocket> bean, Integer integer) {
//    return bean.flatMap(
//      rSocket ->
//      {
//        String data = "m" + integer;
//
//        return rSocket.requestResponse(DefaultPayload.create(data))
//          .flatMap(result -> {
//
//              var responseData = result.getDataUtf8();
//              result.release();
//              // в логах то, что отправляем, и то, что получаем от серверов
//              log.info("response={}", responseData);
//              return Mono.just(new User());
//            }
//          );
//      }
//    );
//  }
}
