package io.memoria.jutils.jtext.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.jutils.jcore.text.Json;
import io.memoria.jutils.jcore.text.TextException;
import io.vavr.control.Try;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

public record JsonJackson(ObjectMapper mapper) implements Json {

  @Override
  @SuppressWarnings("unchecked")
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    return Try.of(() -> mapper.readValue(str, tClass))
              .mapFailure(Case($(instanceOf(JacksonException.class)), e -> new TextException(e.getMessage())));
  }

  @Override
  public <T> Try<String> serialize(T t) {
    return Try.of(() -> mapper.writeValueAsString(t));
  }
}
