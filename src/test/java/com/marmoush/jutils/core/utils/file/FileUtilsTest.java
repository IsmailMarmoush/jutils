package com.marmoush.jutils.core.utils.file;

import com.marmoush.jutils.core.utils.functional.ReactorVavrUtils;
import io.vavr.control.Try;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Path;

import static com.marmoush.jutils.core.utils.file.FileUtils.*;

public class FileUtilsTest {
  @Test
  public void fileAsStringTest() {
    Assertions.assertEquals("hello\nworld", resource("file.txt").get());
  }

  @Test
  public void appendOrCreateTest() {
    Mono<Try<Path>> helloWorld = writeFile("target/temp.txt", "hello world", Schedulers.elastic());
    StepVerifier.create(helloWorld).expectNextMatches(Try::isSuccess).expectComplete().verify();
    Mono<Try<Boolean>> fileExists = helloWorld.flatMap(ReactorVavrUtils.tryToMonoTry(h -> Mono.just(Try.of(() -> h.toFile()
                                                                                                                  .exists()))));
    StepVerifier.create(fileExists).expectNext(Try.success(true)).expectComplete().verify();

    String tempFile = file("target/temp.txt").get();
    Assertions.assertEquals("hello world", tempFile);
  }
}