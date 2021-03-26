package channel;

import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;

import static channel.ChannelHandler.customSocketAcceptor;
import static java.lang.Thread.sleep;
import static utils.PortUtils.SERVER_2_PORT;

@Slf4j
public class ChannelServer2 {


  public static void main(String[] args) throws InterruptedException {

    var disposable = RSocketServer.create(customSocketAcceptor())
      .payloadDecoder(PayloadDecoder.ZERO_COPY)
      .bind(TcpServerTransport.create(SERVER_2_PORT))
      .subscribe();


    sleep(5_000_000);

  }

}
