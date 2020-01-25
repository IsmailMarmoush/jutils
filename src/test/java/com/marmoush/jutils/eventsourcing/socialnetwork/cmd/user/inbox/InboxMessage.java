package com.marmoush.jutils.eventsourcing.socialnetwork.cmd.user.inbox;

import com.marmoush.jutils.eventsourcing.socialnetwork.cmd.msg.Message;

public class InboxMessage {
  public final Message ms;
  public final boolean seen;

  public InboxMessage(Message ms, boolean seen) {
    this.ms = ms;
    this.seen = seen;
  }
}
