package io.memoria.jutils.adapter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.memoria.jutils.adapter.Tests;
import io.memoria.jutils.core.json.Json;
import org.junit.jupiter.api.Test;

import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PeriodAdapterTest {
  private final Gson gson = Tests.registerPeriodAdapter(new GsonBuilder()).create();
  private final Json parser = new JsonGson(gson);
  // Given
  private final String periodJson = "\"P1Y2M25D\"";
  private final Period period = Period.of(1, 2, 25);

  @Test
  public void deserializer() {
    // When
    Period deserializedPeriod = parser.deserialize(periodJson, Period.class).get();
    // Then
    assertEquals(period, deserializedPeriod);
  }

  @Test
  public void serializer() {
    // When
    String serializedPeriod = parser.serialize(period);
    // Then
    assertEquals(periodJson, serializedPeriod);
  }
}
