package response;

import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.lang.Thread.sleep;
import static response.RARHandler.requestResponse;
import static utils.PortUtils.SERVER_1_PORT;

@Slf4j
public class RARServer1 {


  public static void main(String[] args) throws InterruptedException {

    var disposable = RSocketServer.create(requestResponse("s1"))
      .payloadDecoder(PayloadDecoder.ZERO_COPY)
      .bind(TcpServerTransport.create(SERVER_1_PORT))
      .subscribe();


    sleep(5_000_000);

  }

}
