package io.memoria.jutils.jkafka;

import io.memoria.jutils.jcore.msgbus.MsgBus;
import io.memoria.jutils.jcore.vavr.ReactorVavrUtils;
import io.vavr.collection.List;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Map;

import static io.memoria.jutils.jkafka.KafkaUtils.createConsumer;
import static io.memoria.jutils.jkafka.KafkaUtils.pollOnce;
import static io.memoria.jutils.jkafka.KafkaUtils.sendRecords;

public class KafkaMsgBus implements MsgBus {
  public final String topic;
  public final int partition;
  public final String TRANSACTION_ID;
  private final KafkaProducer<String, String> producer;
  private final Map<String, Object> consumerConfig;
  private final Duration timeout;
  private final Scheduler scheduler;

  public KafkaMsgBus(Map<String, Object> producerConfig,
                     Map<String, Object> consumerConfig,
                     String topic,
                     int partition,
                     Duration reqTimeout,
                     Scheduler scheduler) {
    this.topic = topic;
    this.partition = partition;
    this.TRANSACTION_ID = topic + "_" + partition;
    producerConfig.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TRANSACTION_ID);
    this.producer = new KafkaProducer<>(producerConfig);
    this.producer.initTransactions();
    this.consumerConfig = consumerConfig;
    this.timeout = reqTimeout;
    this.scheduler = scheduler;
  }

  @Override
  public Mono<List<String>> publish(List<String> msgs) {
    return Mono.fromCallable(() -> sendRecords(producer, topic, partition, msgs, timeout)).subscribeOn(scheduler);
  }

  @Override
  public Flux<String> subscribe(long offset) {
    return Mono.fromCallable(() -> createConsumer(consumerConfig, topic, partition, offset, timeout))
               .flatMapMany(consumer -> Mono.fromCallable(() -> pollOnce(consumer, topic, partition, timeout)).repeat())
               .concatMap(Flux::fromIterable)
               .subscribeOn(scheduler);
  }

  @Override
  public Mono<String> last() {
    return Mono.fromCallable(() -> KafkaUtils.lastMessage(consumerConfig, topic, partition, timeout))
               .flatMap(ReactorVavrUtils::toMono);
  }
}
