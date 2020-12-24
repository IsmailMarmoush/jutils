package io.memoria.jutils.core.eventsourcing;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Try;

public interface Decider<S extends Entity<?>, C extends Command> extends Function2<S, C, Try<List<Event>>> {}