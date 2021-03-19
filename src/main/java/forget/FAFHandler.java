package forget;

import io.rsocket.SocketAcceptor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class FAFHandler {
  protected static SocketAcceptor fireAndForget(String serverName) {
    return SocketAcceptor.forFireAndForget(payload -> {

      return Mono.defer(() -> {
        var data = payload.getDataUtf8();
        payload.release();

        log.info("Received request data {}", data);
        return Mono.empty();
      })
        .then()
        .subscribeOn(Schedulers.newParallel("fire-and-forget", 10));

    });
  }
}
