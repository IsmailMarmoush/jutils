package io.memoria.jutils.jcore.eventsourcing;

import io.vavr.Function1;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface CommandHandler<C extends Command> extends Function1<C, Mono<Void>> {}