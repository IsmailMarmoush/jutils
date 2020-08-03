package io.memoria.jutils.adapter.file;

import io.memoria.jutils.adapter.Tests;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWriterTest {

  @Test
  public void appendOrCreateTest() {
    Mono<Path> helloWorld = Tests.FILE_WRITER.writeFile(Paths.get("target/temp.txt"), "hello world");
    StepVerifier.create(helloWorld).expectNextCount(1).expectComplete().verify();

    Mono<Boolean> fileExists = helloWorld.map(h -> h.toFile().exists());
    StepVerifier.create(fileExists).expectNext(true).expectComplete().verify();

    Mono<String> tempFile = Tests.FILE_READER.file(Paths.get("target/temp.txt"));
    StepVerifier.create(tempFile).expectNext("hello world").expectComplete().verify();
  }
}
