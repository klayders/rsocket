package forget;

import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Schedulers;

import static forget.FAFHandler.fireAndForget;
import static java.lang.Thread.sleep;
import static utils.PortUtils.SERVER_3_PORT;

@Slf4j
public class FAFServer3 {


  public static void main(String[] args) throws InterruptedException {

    var disposable = RSocketServer.create(fireAndForget("s3"))
      .payloadDecoder(PayloadDecoder.ZERO_COPY)
      .bind(TcpServerTransport.create(SERVER_3_PORT))
      .subscribe();


    sleep(5_000_000);

  }

}
