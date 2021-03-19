package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.User;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.lang.Thread.sleep;

@Slf4j
public class ExamServer2 {

  private static final ObjectMapper mapper = new ObjectMapper();


  public static void main(String[] args) throws InterruptedException {

    var res = RSocketServer.create(requestResponse())
      .payloadDecoder(PayloadDecoder.ZERO_COPY)
      .bind(TcpServerTransport.create(12346))
      .publishOn(Schedulers.newParallel("pp", 5))
      .subscribeOn(Schedulers.newParallel("ss", 5))
      .subscribe();


    sleep(5_000_000);

  }

  private static SocketAcceptor requestResponse() {
    return SocketAcceptor.forRequestResponse(payload -> {
      var data = payload.getDataUtf8();
      payload.release();


      final String name = "s2" + data;


      log.info("Received request data {}", data);

      var responsePayload = DefaultPayload.create(name);
      return Mono.just(responsePayload);
//      return Mono.empty();
    });
  }
}
