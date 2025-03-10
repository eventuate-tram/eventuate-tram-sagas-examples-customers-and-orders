package io.eventuate.examples.tram.sagas.customersandorders.orders.proxies.customers;

import io.eventuate.common.json.mapper.JSonMapper;
import io.eventuate.tram.commands.common.ReplyMessageHeaders;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import org.junit.jupiter.api.Assertions;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TestReplyConsumer {

  private static BlockingQueue<Message> messages = new LinkedBlockingQueue<>();

  private final String subscriberId;
  private final MessageConsumer messageConsumer;
  private final String channel;

  public TestReplyConsumer(String subscriberId, String channel, MessageConsumer messageConsumer) {
    this.subscriberId = subscriberId;
    this.messageConsumer = messageConsumer;
    this.channel = channel;
  }

  @PostConstruct
  private void subscriber() {
    messageConsumer.subscribe(subscriberId, Set.of(channel), message -> {
      messages.add(message);
    });
  }

  public Message assertMessageReceived() {
    try {
      return messages.poll(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  void assertReplyOfTypeReceived(Class<?> expectedReplyType) {
    var message = assertMessageReceived();
    String replyType = message.getRequiredHeader(ReplyMessageHeaders.REPLY_TYPE);
    Assertions.assertEquals(expectedReplyType.getName(), replyType);
    var replyObject = JSonMapper.fromJsonByName(message.getPayload(), replyType);
    Assertions.assertNotNull(replyObject);
  }
}
