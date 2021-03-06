package io.memoria.jutils.jcore.stream;

import reactor.core.publisher.Flux;

public interface StreamRepo {
  Flux<Msg> publish(Flux<Msg> msg);

  Flux<Msg> subscribe(long offset);
}
