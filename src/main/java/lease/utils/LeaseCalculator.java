package lease.utils;

import io.rsocket.lease.Lease;
import io.rsocket.lease.LeaseStats;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class LeaseCalculator implements Function<Optional<LeaseStats>, Flux<Lease>> {
  final String tag;
  final Sinks.Many<String> queue;

  public LeaseCalculator(String tag, Sinks.Many<String> queue) {
    this.tag = tag;
    this.queue = queue;
  }

  @Override
  public Flux<Lease> apply(Optional<LeaseStats> leaseStats) {
    log.info("{} stats are {}", tag, leaseStats.isPresent() ? "present" : "absent");
    Duration ttlDuration = Duration.ofSeconds(5);
    // The interval function is used only for the demo purpose and should not be
    // considered as the way to issue leases.
    // For advanced RateLimiting with Leasing
    // consider adopting https://github.com/Netflix/concurrency-limits#server-limiter
    return Flux.interval(Duration.ZERO, ttlDuration.dividedBy(2))
      .handle(
        (__, sink) -> {
          // put queue.remainingCapacity() + 1 here if you want to observe that app is
          // terminated  because of the queue overflowing
          int requests = queue.currentSubscriberCount();

          // reissue new lease only if queue has remaining capacity to
          // accept more requests
          if (requests > 0) {
            long ttl = ttlDuration.toMillis();
            sink.next(Lease.create((int) ttl, requests));
          }
        });
  }
}

