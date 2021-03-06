package io.memoria.jutils.jcore.eventsourcing.repo;

import io.memoria.jutils.jcore.eventsourcing.Event;
import io.memoria.jutils.jcore.id.Id;
import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface EventRepo {
  Mono<Integer> add(List<Event> events);

  Mono<List<Event>> find();

  Mono<List<Event>> find(Id aggId);
}
