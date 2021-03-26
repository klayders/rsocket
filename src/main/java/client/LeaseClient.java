package client;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.lease.Leases;
import io.rsocket.lease.MissingLeaseException;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.ByteBufPayload;
import lease.utils.LeaseReceiver;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.Objects;

@Slf4j
public class LeaseClient {
  private static final String CLIENT_TAG = "client";

  public static void main(String[] args) {
    var receiver = new LeaseReceiver(CLIENT_TAG);
    RSocket clientRSocket =
      RSocketConnector.create()
        .lease(() -> Leases.create().receiver(receiver))
        .connect(TcpClientTransport.create("localhost", 8765))
        .block();

    Objects.requireNonNull(clientRSocket);

    // generate stream of fnfs
    Flux.generate(
      () -> 0L,
      (state, sink) -> {
        sink.next(state);
        return state + 1;
      })
      // here we wait for the first lease for the responder side and start execution
      // on if there is allowance
      .delaySubscription(receiver.notifyWhenNewLease().then())
      .concatMap(
        tick -> {
          log.info("Requesting FireAndForget({})", tick);
          return Mono.defer(() -> clientRSocket.fireAndForget(ByteBufPayload.create("" + tick)))
            .retryWhen(
              Retry.indefinitely()
                // ensures that error is the result of missed lease
                .filter(t -> t instanceof MissingLeaseException)
                .doBeforeRetryAsync(
                  rs -> {
                    // here we create a mechanism to delay the retry until
                    // the new lease allowance comes in.
                    log.info("Ran out of leases {}", rs);
                    return receiver.notifyWhenNewLease().then();
                  }));
        })
      .blockLast();
  }
}
