package io.memoria.jutils.nats;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener.Events;
import io.nats.client.Consumer;
import io.nats.client.ErrorListener;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class NatsCore {
  private static final Logger log = LoggerFactory.getLogger(NatsCore.class.getName());
  private static final ErrorListener err = new ErrorListener() {
    public void errorOccurred(Connection conn, String type) {
      log.error("Error {}", type);
    }

    public void exceptionOccurred(Connection conn, Exception exp) {
      log.error("Exception", exp);
    }

    public void slowConsumerDetected(Connection conn, Consumer consumer) {
      var url = Option.of(conn.getConnectedUrl()).getOrElse("");
      log.error("Slow consumer on connection {}", url);
    }
  };

  public static Connection createConnection(String server,
                                            Duration conTimeout,
                                            Duration reconTimeout,
                                            int bufferSize,
                                            Duration pingInterval) throws IOException, InterruptedException {

    var config = new Options.Builder().server(server)
                                      .connectionTimeout(conTimeout)
                                      .reconnectWait(reconTimeout)
                                      .bufferSize(bufferSize)
                                      .pingInterval(pingInterval)
                                      .connectionListener(NatsCore::onConnectionEvent)
                                      .errorListener(err)
                                      .build();
    return Nats.connect(config);
  }

  private NatsCore() {}

  private static void onConnectionEvent(Connection conn, Events type) {
    log.info("Status change {}", type);
  }
}