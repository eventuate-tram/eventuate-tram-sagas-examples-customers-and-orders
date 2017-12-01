package io.eventuate.examples.tram.sagas.ordersandcustomers;

public class TramCommandsAndEventsIntegrationData {

  private static final String postfix = "id";
  private String commandDispatcherId = "command-dispatcher-" + postfix;
  private String commandChannel = "command-channel-" + postfix;
  private String aggregateDestination = "aggregate-destination-" + postfix;
  private String eventDispatcherId  = "event-dispatcher-" + postfix;

  public String getAggregateDestination() {
    return aggregateDestination;
  }


  public String getCommandDispatcherId() {
    return commandDispatcherId;
  }

  public String getCommandChannel() {
    return commandChannel;
  }

  public String getEventDispatcherId() {
    return eventDispatcherId;
  }
}
