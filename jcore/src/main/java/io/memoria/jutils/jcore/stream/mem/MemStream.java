package io.memoria.jutils.jcore.stream.mem;

import io.memoria.jutils.jcore.stream.Msg;
import io.memoria.jutils.jcore.stream.StreamRepo;
import reactor.core.publisher.Flux;

import java.util.List;

public record MemStream(List<Msg> store) implements StreamRepo {

  @Override
  public Flux<Msg> publish(Flux<Msg> msgs) {
    return msgs.map(msg -> {
      store.add(msg);
      return msg;
    });
  }

  @Override
  public Flux<Msg> subscribe(long offset) {
    return Flux.fromIterable(store).skip(offset);
  }
}
