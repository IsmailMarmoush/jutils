package io.memoria.jutils.messaging.domain.port;

import io.memoria.jutils.messaging.domain.Message;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MsgSender {
  Flux<Option<? extends Message>> send(String topic, String partition, Flux<? extends Message> msgFlux);

  Mono<Void> close();
}
