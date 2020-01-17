package com.marmoush.jutils.adapter.msgbus.Nats;

import com.marmoush.jutils.domain.port.msgbus.MsgProducer;
import com.marmoush.jutils.domain.value.msg.Msg;
import com.marmoush.jutils.domain.value.msg.ProducerResp;
import com.marmoush.jutils.utils.yaml.YamlConfigMap;
import io.nats.client.Connection;
import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.marmoush.jutils.adapter.msgbus.Nats.NatsConnection.CHANNEL_SEPARATOR;
import static com.marmoush.jutils.adapter.msgbus.Nats.NatsConnection.create;
import static com.marmoush.jutils.utils.functional.ReactorVavrUtils.blockingToMono;

public class NatsMsgProducer implements MsgProducer<Void> {
  private final Connection nc;
  private final Scheduler scheduler;
  private final Duration timeout;

  public NatsMsgProducer(YamlConfigMap map, Scheduler scheduler) throws IOException, InterruptedException {
    this.scheduler = scheduler;
    this.timeout = Duration.ofMillis(map.asMap("reactorNats").asLong("producer.request.timeout"));
    this.nc = create(map);
  }

  @Override
  public Flux<Try<ProducerResp<Void>>> produce(String topic, String partition, Flux<Msg> msgFlux) {
    return msgFlux.publishOn(scheduler).map(msg -> Try.of(() -> {
      nc.publish(topic + CHANNEL_SEPARATOR + partition, msg.value.getBytes(StandardCharsets.UTF_8));
      return new ProducerResp<Void>();
    })).timeout(timeout);
  }

  @Override
  public Mono<Try<Void>> close() {
    return blockingToMono(() -> Try.run(() -> nc.close()), scheduler);
  }
}
