package io.memoria.jutils.jackson.transformer;

import io.vavr.collection.List;

public record Department(List<Employee> employees) {}
