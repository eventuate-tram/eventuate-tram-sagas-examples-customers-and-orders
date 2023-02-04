package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.json.mapper.JSonMapper;
import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.CommandWithDestinationAndType;
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessage;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessaging;

import java.util.Collections;

public class SagaMessagingTestHelper {

  private ContractVerifierMessaging contractVerifierMessaging;

  private SagaCommandProducer sagaCommandProducer;

  private IdGenerator idGenerator;

  public SagaMessagingTestHelper(ContractVerifierMessaging contractVerifierMessaging, SagaCommandProducer sagaCommandProducer, IdGenerator idGenerator) {
    this.contractVerifierMessaging = contractVerifierMessaging;
    this.sagaCommandProducer = sagaCommandProducer;
    this.idGenerator = idGenerator;
  }

  public <C extends Command, R> R sendAndReceiveCommand(CommandWithDestination commandWithDestination, Class<R> replyClass, String sagaType) {
    // TODO verify that replyClass is allowed

    String sagaId = idGenerator.genIdAsString();

    String replyTo = sagaType + "-reply";
    sagaCommandProducer.sendCommands(sagaType, sagaId, Collections.singletonList(CommandWithDestinationAndType.command(commandWithDestination)), replyTo);

    ContractVerifierMessage response = contractVerifierMessaging.receive(replyTo);

    if (response == null)
      return null;
    String payload = (String) response.getPayload();
    return (R) JSonMapper.fromJson(payload, replyClass);
  }
}
