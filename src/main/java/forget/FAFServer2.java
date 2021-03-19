package forget;

import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Schedulers;

import static forget.FAFHandler.fireAndForget;
import static java.lang.Thread.sleep;
import static utils.PortUtils.SERVER_2_PORT;

@Slf4j
public class FAFServer2 {


  public static void main(String[] args) throws InterruptedException {

    var disposable = RSocketServer.create(fireAndForget())
      .payloadDecoder(PayloadDecoder.ZERO_COPY)
      .bind(TcpServerTransport.create(SERVER_2_PORT))
      .publishOn(Schedulers.newParallel("pp", 5))
      .subscribeOn(Schedulers.newParallel("ss", 5))
      .subscribe();


    sleep(5_000_000);

  }

}
