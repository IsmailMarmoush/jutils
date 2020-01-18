package com.marmoush.jutils.domain.port.msgbus;

import com.marmoush.jutils.domain.value.msg.ConsumerResp;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MsgConsumer<T> {
  Flux<Try<ConsumerResp<T>>> consume(String topicId, String partition, long offset);

  Mono<Try<Void>> close();
}