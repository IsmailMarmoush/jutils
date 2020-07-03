package io.memoria.jutils.security.adapter.argon;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.memoria.jutils.security.domain.port.Hasher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

public record Argon2Hasher(Argon2 argon2, int iterations, int memory, int parallelism, Scheduler scheduler)
        implements Hasher {

  public Argon2Hasher(int iterations, int memory, int parallelism, Scheduler scheduler) {
    this(Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id), iterations, memory, parallelism, scheduler);
  }

  @Override
  public String blockingHash(String password, String salt) {
    String saltedPass = password + salt;
    String hash = argon2.hash(iterations, memory, parallelism, saltedPass.getBytes());
    argon2.wipeArray(saltedPass.toCharArray());
    return hash;
  }

  @Override
  public Mono<String> hash(String password, String salt) {
    return Mono.defer(() -> Mono.just(blockingHash(password, salt)).subscribeOn(scheduler));
  }
}
