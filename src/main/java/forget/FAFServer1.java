package forget;

import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Schedulers;

import static forget.FAFHandler.fireAndForget;
import static java.lang.Thread.sleep;
import static utils.PortUtils.SERVER_1_PORT;

@Slf4j
public class FAFServer1 {


  public static void main(String[] args) throws InterruptedException {

    var disposable = RSocketServer.create(fireAndForget("s1"))
      .payloadDecoder(PayloadDecoder.ZERO_COPY)
      .bind(TcpServerTransport.create(SERVER_1_PORT))
      .subscribe();


    sleep(5_000_000);

  }


}
