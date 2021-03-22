package io.memoria.jutils.jcore.eventsourcing.data.user;

import io.memoria.jutils.jcore.eventsourcing.Command;
import io.memoria.jutils.jcore.id.Id;

public interface UserCommand extends Command {

  record CreateUser(Id commandId, Id userId, String username) implements UserCommand {
    @Override
    public Id aggId() {
      return userId;
    }
  }
}