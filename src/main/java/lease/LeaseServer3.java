package lease;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketServer;
import io.rsocket.lease.Leases;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lease.utils.LeaseCalculator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static lease.LeaseHandler.publishMessage;
import static utils.PortUtils.SERVER_3_PORT;

@Slf4j
public class LeaseServer3 {

  private static final String SERVER_TAG = "s1";

  private static final Sinks.Many<String> MASSAGE_QUEUE = Sinks.many()
    .unicast()
    .onBackpressureBuffer();


  public static void main(String[] args) {

    var server =
      RSocketServer.create(
        (setup, sendingSocket) ->
          Mono.just(
            new RSocket() {
              @Override
              public Mono<Void> fireAndForget(Payload payload) {
                return publishMessage(payload, sendingSocket, MASSAGE_QUEUE);
              }
            }))
        .lease(() -> Leases.create().sender(new LeaseCalculator(SERVER_TAG, MASSAGE_QUEUE)))
        .bindNow(TcpServerTransport.create("localhost", SERVER_3_PORT));
  }

}
