package com.marmoush.jutils.eventsourcing.event;

import io.vavr.*;
import io.vavr.collection.Traversable;

@FunctionalInterface
public interface EventHandler<T1, T2 extends Event> extends Function2<T1, T2, T1> {
  default T1 apply(T1 state, Traversable<T2> e) {
    return e.foldLeft(state, this);
  }

  default Function1<Traversable<T2>, T1> curried(T1 state) {
    return (Traversable<T2> e) -> apply(state, e);
  }
}