package forget;

import io.rsocket.SocketAcceptor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class FAFHandler {
  protected static SocketAcceptor fireAndForget() {
    return SocketAcceptor.forFireAndForget(payload -> {
      var data = payload.getDataUtf8();
      payload.release();

      log.info("Received request data {}", data);
      return Mono.empty();
    });
  }
}
