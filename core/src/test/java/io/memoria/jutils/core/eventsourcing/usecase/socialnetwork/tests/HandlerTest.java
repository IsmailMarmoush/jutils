package io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.tests;

import io.memoria.jutils.core.eventsourcing.ESException.ESInvalidOperation;
import io.memoria.jutils.core.eventsourcing.InMemoryEventStore;
import io.memoria.jutils.core.eventsourcing.cmd.CommandHandler;
import io.memoria.jutils.core.eventsourcing.event.Event;
import io.memoria.jutils.core.eventsourcing.event.EventStore;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.User.Visitor;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserCommand;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserCommand.CreateAccount;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserDecider;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvent.AccountCreated;
import io.memoria.jutils.core.eventsourcing.usecase.socialnetwork.domain.UserEvolver;
import io.memoria.jutils.core.generator.IdGenerator;
import io.memoria.jutils.core.value.Id;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class HandlerTest {
  private static final Map<String, ArrayList<Event>> db = new HashMap<>();
  private static final EventStore eventStore = new InMemoryEventStore(db);
  private static final IdGenerator idGen = () -> new Id("0");
  private static final String workSpaceAggId = "02";
  private static final CommandHandler<User, UserCommand> handler = new CommandHandler<>(eventStore,
                                                                                        new UserEvolver(),
                                                                                        new UserDecider(idGen),
                                                                                        new Visitor());

  @BeforeEach
  void beforeEach() {
    db.clear();
  }

  @Test
  void handle() {
    // Given
    var cmd = new CreateAccount(idGen.get(), 18);
    // When
    var handleMono = handler.apply(workSpaceAggId, cmd);
    // Then
    StepVerifier.create(handleMono).expectComplete().verify();
    Assertions.assertTrue(db.get(workSpaceAggId).contains(new AccountCreated(new Id("0"), new Id("0"), 18)));
  }

  @Test
  void shouldProduceInvalidOperation() {
    // Given
    var cmd = new CreateAccount(idGen.get(), 18);
    var list = io.vavr.collection.List.<UserCommand>of(cmd, cmd);
    // When
    var handleMono = handler.apply(workSpaceAggId, list);
    // Then
    StepVerifier.create(handleMono).expectError(ESInvalidOperation.class).verify();
  }
}
