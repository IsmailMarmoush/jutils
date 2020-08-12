package io.memoria.jutils.utils.functional;

import io.memoria.jutils.core.JutilsException.NotFound;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static io.memoria.jutils.utils.functional.ReactorVavrUtils.toMono;
import static io.memoria.jutils.utils.functional.ReactorVavrUtils.toVoidMono;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public class ReactorVavrUtilsTest {

  @Test
  public void booleanToMono() {
    var v = toMono(() -> "hello world", new Exception("isFalse"));
    StepVerifier.create(v.apply(true)).expectNext("hello world").expectComplete().verify();
    StepVerifier.create(v.apply(false)).expectErrorMessage("isFalse").verify();
  }

  @Test
  public void booleanToVoidMono() {
    var v = toVoidMono(() -> {}, new Exception("isFalse"));
    StepVerifier.create(v.apply(true)).expectComplete().verify();
    StepVerifier.create(v.apply(false)).expectErrorMessage("isFalse").verify();
  }

  @Test
  public void eitherToMonoTest() {
    Either<Exception, Integer> k = right(23);
    Mono<Integer> integerMono = toMono(k);
    StepVerifier.create(integerMono).expectNext(23).expectComplete().verify();

    k = left(new Exception("exception example"));
    integerMono = toMono(k);
    StepVerifier.create(integerMono).expectError().verify();
  }

  @Test
  public void shorterTryToFluxTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Flux<Try<Integer>>> op1 = t -> Flux.just(Try.success((t + " world").length()));
    Function<Integer, Flux<Try<String>>> op2 = t -> Flux.just(Try.success("count is " + t));
    Flux<Try<String>> tryFlux = Flux.just(h)
                                    .flatMap(ReactorVavrUtils.toTryFlux(op1))
                                    .flatMap(ReactorVavrUtils.toTryFlux(op2));
    StepVerifier.create(tryFlux).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Flux<Try<String>>> opError = t -> Flux.just(Try.failure(new Exception("should fail")));
    tryFlux = tryFlux.flatMap(ReactorVavrUtils.toTryFlux(opError));
    StepVerifier.create(tryFlux).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void shorterTryToMonoTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Mono<Try<Integer>>> op1 = t -> Mono.just(Try.success((t + " world").length()));
    Function<Integer, Mono<Try<String>>> op2 = t -> Mono.just(Try.success("count is " + t));
    Mono<Try<String>> tryMono = Mono.just(h)
                                    .flatMap(ReactorVavrUtils.toTryMono(op1))
                                    .flatMap(ReactorVavrUtils.toTryMono(op2));
    StepVerifier.create(tryMono).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Mono<Try<String>>> opError = t -> Mono.just(Try.failure(new Exception("should fail")));
    tryMono = tryMono.flatMap(ReactorVavrUtils.toTryMono(opError));
    StepVerifier.create(tryMono).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void toMonoFromOption() {
    // Given
    var som = some(20);
    var non = none();
    // When
    var somMono = toMono(som, NotFound.NOT_FOUND);
    var nonMono = toMono(non, NotFound.NOT_FOUND);
    // Then
    StepVerifier.create(somMono).expectNext(20).expectComplete().verify();
    StepVerifier.create(nonMono).expectError(NotFound.class).verify();
  }

  @Test
  public void tryToFluxTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Flux<Try<Integer>>> op1 = t -> Flux.just(Try.success((t + " world").length()));
    Function<Integer, Flux<Try<String>>> op2 = t -> Flux.just(Try.success("count is " + t));
    Flux<Try<String>> tryFlux = Flux.just(h)
                                    .flatMap(k -> ReactorVavrUtils.toTryFlux(k, op1))
                                    .flatMap(r -> ReactorVavrUtils.toTryFlux(r, op2));
    StepVerifier.create(tryFlux).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Flux<Try<String>>> opError = t -> Flux.just(Try.failure(new Exception("should fail")));
    tryFlux = tryFlux.flatMap(k -> ReactorVavrUtils.toTryFlux(k, opError));
    StepVerifier.create(tryFlux).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void tryToMonoTest() {
    var tSuccess = Try.success("hello");
    StepVerifier.create(toMono(tSuccess)).expectNext("hello").expectComplete().verify();
    var tFailure = Try.failure(new Exception("Exception Happened"));
    StepVerifier.create(toMono(tFailure)).expectError(Exception.class).verify();
  }

  @Test
  public void tryToMonoTryTest() {
    Try<String> h = Try.success("hello");
    Function<String, Mono<Try<Integer>>> op1 = t -> Mono.just(Try.success((t + " world").length()));
    Function<Integer, Mono<Try<String>>> op2 = t -> Mono.just(Try.success("count is " + t));
    Mono<Try<String>> tryMono = Mono.just(h)
                                    .flatMap(k -> ReactorVavrUtils.toTryMono(k, op1))
                                    .flatMap(r -> ReactorVavrUtils.toTryMono(r, op2));
    StepVerifier.create(tryMono).expectNext(Try.success("count is 11")).expectComplete().verify();
    // Failure
    Function<String, Mono<Try<String>>> opError = t -> Mono.just(Try.failure(new Exception("should fail")));
    tryMono = tryMono.flatMap(k -> ReactorVavrUtils.toTryMono(k, opError));
    StepVerifier.create(tryMono).expectNextMatches(Try::isFailure).expectComplete().verify();
  }

  @Test
  public void tryToMonoVoidTest() {
    Mono<Try<String>> original = Mono.just(Try.success("one"));
    Function<String, Mono<Void>> deferredOp = (String content) -> Mono.empty();
    Function<Throwable, Mono<Void>> throwable = t -> Mono.just(Try.failure(new Exception("should not fail"))).then();
    Mono<Void> voidMono = original.flatMap(toVoidMono(deferredOp, throwable));
    StepVerifier.create(voidMono).expectComplete().verify();
  }
}