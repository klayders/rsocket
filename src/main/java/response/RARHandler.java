package response;

import io.rsocket.SocketAcceptor;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.lang.Thread.sleep;

@Slf4j
public class RARHandler {

  protected static SocketAcceptor requestResponse(String serverName) {
    return SocketAcceptor.forRequestResponse(payload -> Mono.defer(() -> {

      try {
        sleep(9_000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      var data = payload.getDataUtf8();
      payload.release();

      final String name = serverName + data;

      log.info("Received request data {}", data);

      var responsePayload = DefaultPayload.create(name);
      return Mono.just(responsePayload);
    })
      .subscribeOn(Schedulers.newParallel("parallels", 10)));
  }
}
