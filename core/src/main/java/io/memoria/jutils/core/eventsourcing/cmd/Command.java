package io.memoria.jutils.core.eventsourcing.cmd;

import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.state.State;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Try;

public interface Command<T extends State> extends Function1<T, Try<List<Event<T>>>> {
  Try<List<Event<T>>> apply(T t);
}
