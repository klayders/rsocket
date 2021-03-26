package channel;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static channel.ChannelHandler.customSocketAcceptor;
import static java.lang.Thread.sleep;
import static utils.PortUtils.SERVER_1_PORT;

@Slf4j
public class ChannelServer1 {

  private static final Sinks.Many<String> MASSAGE_QUEUE = Sinks.many()
    .multicast()
    .onBackpressureBuffer();

  public static void main(String[] args) throws InterruptedException {
    var disposable = RSocketServer.create(customSocketAcceptor())
      .payloadDecoder(PayloadDecoder.ZERO_COPY)
      .bind(TcpServerTransport.create(SERVER_1_PORT))
      .subscribe();


    sleep(5_000_000);
  }



}
