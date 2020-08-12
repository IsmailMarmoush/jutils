package io.memoria.jutils.core.eventsourcing.event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventReadRepo<K, E extends Event> {
  Mono<Boolean> aggExists(K k, String aggId);

  Flux<E> aggStream(K k, String aggId);

  Mono<Boolean> exists(K k);

  Flux<E> stream(K k);
}