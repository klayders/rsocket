package lease.utils;

import io.rsocket.lease.Lease;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.util.function.Consumer;

@Slf4j
public class LeaseReceiver implements Consumer<Flux<Lease>> {
  final String tag;
  final ReplayProcessor<Lease> lastLeaseReplay = ReplayProcessor.cacheLast();

  public LeaseReceiver(String tag) {
    this.tag = tag;
  }

  @Override
  public void accept(Flux<Lease> receivedLeases) {
    receivedLeases.subscribe(
      l -> {
        log.info(
          "{} received leases - ttl: {}, requests: {}",
          tag,
          l.getTimeToLiveMillis(),
          l.getAllowedRequests());
        lastLeaseReplay.onNext(l);
      });
  }

  /**
   * This method allows to listen to new incoming leases and delay some action (e.g . retry) until
   * new valid lease has come in
   */
  public Mono<Lease> notifyWhenNewLease() {
    return lastLeaseReplay.filter(Lease::isValid).next();
  }
}
