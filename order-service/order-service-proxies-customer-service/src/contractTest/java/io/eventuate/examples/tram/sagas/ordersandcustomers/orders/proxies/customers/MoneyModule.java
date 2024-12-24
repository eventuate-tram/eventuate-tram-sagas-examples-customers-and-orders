package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.proxies.customers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import io.eventuate.examples.common.money.Money;

import java.io.IOException;

public class MoneyModule extends SimpleModule {

  class MoneyDeserializer extends StdScalarDeserializer<Money> {

    protected MoneyDeserializer() {
      super(Money.class);
    }

    @Override
    public Money deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.VALUE_STRING) {
        String str = jp.getText().trim();
        if (str.isEmpty())
          return null;
        else
          return new Money(str);
      } else
        throw ctxt.wrongTokenException(jp, getValueClass(), JsonToken.VALUE_STRING, "Expected JSON String");
    }
  }

  class MoneySerializer extends StdScalarSerializer<Money> {
    public MoneySerializer() {
      super(Money.class);
    }

    @Override
    public void serialize(Money value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
      jgen.writeString(value.getAmount().toString());
    }
  }

  @Override
  public String getModuleName() {
    return "MoneyModule";
  }

  public MoneyModule() {
    addDeserializer(Money.class, new MoneyDeserializer());
    addSerializer(Money.class, new MoneySerializer());
  }

}