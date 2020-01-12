package com.marmoush.jutils.adapter.msgbus.kafka;

import com.marmoush.jutils.domain.port.msgbus.MsgConsumer;
import com.marmoush.jutils.domain.port.msgbus.MsgPublisher;
import com.marmoush.jutils.domain.value.msg.ConsumeResponse;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.PublishResponse;
import com.marmoush.jutils.utils.yaml.YamlUtils;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Random;

public class ReactiveKafkaAPIIT {
  private final Map<String, Object> config = YamlUtils.parseYamlResource("kafka.yaml").get();
  public final String KAFKA_TOPIC = "topic-" + new Random().nextInt(100);
  private final int MSG_COUNT = 3;
  private final int PARTITION = 0;

  @Test
  public void kafkaApiTest() {
    @SuppressWarnings("unchecked")
    var publisherConf = (LinkedHashMap<String, Object>) config.get("publisher").get();
    MsgPublisher msgPublisher = new KafkaMsgPublisher(HashMap.ofAll(publisherConf),
                                                      Schedulers.elastic(),
                                                      Duration.ofSeconds(1));

    @SuppressWarnings("unchecked")
    var consumerConf = (LinkedHashMap<String, Object>) config.get("consumer").get();
    MsgConsumer msgConsumer = new KafkaMsgConsumer(HashMap.ofAll(consumerConf),
                                                   Schedulers.elastic(),
                                                   Duration.ofSeconds(1));

    var msgs = Flux.interval(Duration.ofMillis(10)).map(i -> new Msg(i + "", "Msg number" + i)).take(MSG_COUNT);
    Flux<Try<PublishResponse>> kafkaPublisher = msgPublisher.publish(msgs, KAFKA_TOPIC, PARTITION);
    Flux<Try<ConsumeResponse>> kafkaConsumer = msgConsumer.consume(KAFKA_TOPIC, PARTITION, 0).take(MSG_COUNT);

    StepVerifier.create(kafkaPublisher).expectNextCount(MSG_COUNT).expectComplete().verify();
    StepVerifier.create(kafkaConsumer).expectNextCount(MSG_COUNT).expectComplete().verify();
  }
}