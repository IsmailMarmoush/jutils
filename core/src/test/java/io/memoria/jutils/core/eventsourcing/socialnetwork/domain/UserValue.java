package io.memoria.jutils.core.eventsourcing.socialnetwork.domain;

import io.memoria.jutils.core.value.Id;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public interface UserValue {
  record Account(int age, Set<Id> friends, Set<Id> messages) implements UserValue {
    public Account(int age) {
      this(age, HashSet.empty(), HashSet.empty());
    }

    public boolean canAddFriend(Id friendId) {
      return !this.friends.contains(friendId);
    }

    public boolean canSendMessageTo(Id friendId) {
      return this.friends.contains(friendId);
    }

    public Account withNewFriend(Id friendId) {
      return new Account(age, friends.add(friendId), messages);
    }

    public Account withNewMessage(Id messageId) {
      return new Account(age, friends, messages.add(messageId));
    }
  }

  record Visitor() implements UserValue {}
}
