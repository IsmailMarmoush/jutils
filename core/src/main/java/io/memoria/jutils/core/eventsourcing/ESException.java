package io.memoria.jutils.core.eventsourcing;

/**
 * Eventsourcing Exception
 */
public class ESException extends Exception {
  public static class ESNoHandlerAvailable extends ESException {
    private ESNoHandlerAvailable(Command command) {
      super("No handler available for the command: %s ".formatted(command.getClass().getSimpleName()));
    }
  }

  public static class ESNoDeciderAvailable extends ESException {
    private ESNoDeciderAvailable(State state, Command command) {
      super("No decider available for the command: %s and state: %s".formatted(command.getClass().getSimpleName(),
                                                                               state.getClass().getSimpleName()));
    }
  }

  public static ESException create(String message) {
    return new ESException(message);
  }

  public static ESNoDeciderAvailable noDeciderAvailable(State state, Command command) {
    return new ESNoDeciderAvailable(state, command);
  }

  public static ESNoHandlerAvailable noHandlerAvailable(Command command) {
    return new ESNoHandlerAvailable(command);
  }

  private ESException(String message) {
    super(message);
  }
}
