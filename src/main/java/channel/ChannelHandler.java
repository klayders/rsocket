package channel;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ChannelHandler {


  private static final Sinks.Many<String> MASSAGE_QUEUE = Sinks.many()
    .multicast()
    .onBackpressureBuffer();

  protected static SocketAcceptor customSocketAcceptor() {
    return (stats, rSocket) -> {
      return Mono.just(new RSocket() {
        @Override
        public Mono<Void> fireAndForget(Payload payload) {
          var dataUtf8 = payload.getDataUtf8();
          var emitResult = MASSAGE_QUEUE.tryEmitNext(dataUtf8);
          log.info("fireAndForget: isFailure={}, receive={}", emitResult.isFailure(), dataUtf8);
          return Mono.empty();
        }

        @Override
        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
          return MASSAGE_QUEUE.asFlux()
            .doOnNext(str -> log.info("requestChannel: sending={}", str))
            .map(DefaultPayload::create);
        }
      });
    };
  }

}

