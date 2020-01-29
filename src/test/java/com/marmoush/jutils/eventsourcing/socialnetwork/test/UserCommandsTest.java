package com.marmoush.jutils.eventsourcing.socialnetwork.test;

import com.marmoush.jutils.eventsourcing.domain.port.CommandHandler;
import com.marmoush.jutils.eventsourcing.domain.value.Event;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.UserCommand.*;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.UserEvent.FriendAdded;
import com.marmoush.jutils.eventsourcing.socialnetwork.domain.user.inbox.Inbox;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.*;

import static com.marmoush.jutils.general.domain.error.AlreadyExists.ALREADY_EXISTS;

public class UserCommandsTest {
  private static final String ALEX = "alex";
  private static final String BOB = "bob";
  private static final int ALEX_AGE = 19;
  private static CommandHandler<User, UserCommand, Event> commandHandler = new UserCommandHandler();

  @Test
  public void addFriendTest() {
    var user = new User(ALEX, ALEX_AGE);
    var events = commandHandler.apply(user, new AddFriend(ALEX, BOB));
    Assertions.assertEquals(Try.success(List.of(new FriendAdded(ALEX, BOB))), events);

    var otherUser = new User(ALEX, ALEX_AGE, List.of(BOB), new Inbox());
    var otherEvents = commandHandler.apply(otherUser, new AddFriend(ALEX, BOB));
    Assertions.assertEquals(Try.failure(ALREADY_EXISTS), otherEvents);
  }

  @Test
  void sendMessageTest() {
    var user = new User(ALEX, ALEX_AGE, List.of(BOB), new Inbox());
    var events = commandHandler.apply(user, new SendMessage(ALEX, BOB, "hello"));
    Assertions.assertEquals(Try.success(List.of(new UserEvent.MessageCreated(ALEX, BOB, "hello"))), events);
  }
}