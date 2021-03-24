package stream;

import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;
import static stream.StreamHandler.fromClientRequestStream;
import static stream.StreamHandler.requestStream;
import static utils.PortUtils.SERVER_1_PORT;

@Slf4j
public class SteamServer1 {


  public static void main(String[] args) throws InterruptedException {

//    var disposable = RSocketServer.create((setup, rsocket) -> fromClientRequestStream(rsocket, "s2"))
    var disposable = RSocketServer.create(requestStream("s3"))
      .payloadDecoder(PayloadDecoder.ZERO_COPY)
      .bind(TcpServerTransport.create(SERVER_1_PORT))
      .subscribe();


    sleep(5_000_000);

  }

}
