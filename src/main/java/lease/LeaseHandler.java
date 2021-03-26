package lease;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
public class LeaseHandler {

  protected static Mono<Void> publishMessage(Payload payload, RSocket sendingSocket, Sinks.Many<String> massageQueue) {
    // add element. if overflows errors and terminates execution
    // specifically to show that lease can limit rate of fnf requests in
    // that example
    try {
      var emitResult = massageQueue.tryEmitNext(payload.getDataUtf8());
      if (emitResult.isFailure()) {
        log.error("Queue has been overflowed. Terminating execution");
        sendingSocket.dispose();
      }
    } finally {
      payload.release();
    }
    return Mono.empty();
  }
}
