package stream;

import io.rsocket.SocketAcceptor;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.math.BigInteger;

@Slf4j
public class StreamHandler {

  protected static SocketAcceptor requestStream(String serverName) {
    return SocketAcceptor.forRequestStream(payload -> {

        return Flux.defer(() -> {
            return Flux.range(1, 20)
              .doOnRequest(value -> log.info("Received Request For: {}", value))
              .map(i -> {
                log.info("Sending: {}", i);
                return DefaultPayload.create(serverName + " count=" + i);
              });
          }
        )
          .subscribeOn(Schedulers.newParallel("request-stream-parallel", 10));
      }
    );
  }
}

