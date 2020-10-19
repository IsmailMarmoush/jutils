package io.memoria.jutils.jackson.transformer.yaml;

import io.memoria.jutils.jackson.Tests;
import io.memoria.jutils.jackson.transformer.Employee.Engineer;
import io.memoria.jutils.jackson.transformer.Employee.Manager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlJacksonTest {
  @Test
  @DisplayName("App config nested values should be deserialized correctly")
  void appConfig() {
    var appConfig = Tests.yaml.deserialize(Tests.YAML_APP_CONFIG, AppConfig.class).get();
    assertEquals("hello world", appConfig.subName());
    assertEquals(List.of("hi", "hello", "bye"), appConfig.subList());
  }

  @Test
  void serializeObject() {

  }

  @Test
  void toEmployee() {
    // When
    var engineer = Tests.yaml.deserialize(Tests.YAML_ENGINEER, Engineer.class).get();
    // Then
    Assertions.assertEquals("bob", engineer.name());
    Assertions.assertEquals(io.vavr.collection.List.of("fix issue 1", "Fix issue 2"), engineer.tasks());
  }

  @Test
  void toManager() {
    // When
    var manager = Tests.yaml.deserialize(Tests.YAML_MANAGER, Manager.class).get();
    // Then
    Assertions.assertEquals("Annika", manager.name());
    Assertions.assertEquals(new Engineer("bob", io.vavr.collection.List.of("fix issue 1", "Fix issue 2")), manager.team().get(0));
  }
}