package io.memoria.jutils.messaging.adapter.kafka;

import io.memoria.jutils.core.utils.yaml.YamlConfigMap;
import io.memoria.jutils.messaging.domain.entity.Msg;
import io.memoria.jutils.messaging.domain.port.MsgProducer;
import io.memoria.jutils.core.utils.functional.ReactorVavrUtils;
import io.vavr.control.Try;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KafkaMsgProducer implements MsgProducer {
  private final KafkaProducer<String, String> kafkaProducer;
  private final Scheduler scheduler;
  private final Duration timeout;

  public KafkaMsgProducer(YamlConfigMap map, Scheduler scheduler) {
    this.scheduler = scheduler;
    this.timeout = Duration.ofMillis(map.asYamlConfigMap("reactorKafka").asLong("producer.request.timeout"));
    this.kafkaProducer = new KafkaProducer<>(map.asYamlConfigMap("kafka").asJavaMap("producer"));
  }

  @Override
  public Flux<Try<Void>> produce(String topic, String partitionStr, Flux<Msg> msgFlux) {
    return Try.of(() -> Integer.parseInt(partitionStr))
              .map(partition -> msgFlux.publishOn(scheduler)
                                       .map(msg -> new ProducerRecord<>(topic, partition, msg.id, msg.value))
                                       .map(prodRec -> Try.run(() -> send(prodRec))))
              .getOrElseGet(t -> Flux.just(Try.failure(t)));
  }

  @Override
  public Mono<Try<Void>> close() {
    return ReactorVavrUtils.blockingToMono(() -> Try.run(() -> kafkaProducer.close(timeout)), scheduler);
  }

  private RecordMetadata send(ProducerRecord<String, String> prodRec)
          throws InterruptedException, ExecutionException, TimeoutException {
    return kafkaProducer.send(prodRec).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
  }
}