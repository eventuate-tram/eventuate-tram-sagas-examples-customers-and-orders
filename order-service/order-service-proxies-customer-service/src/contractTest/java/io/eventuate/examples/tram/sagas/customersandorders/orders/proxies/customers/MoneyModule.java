package io.eventuate.examples.tram.sagas.customersandorders.orders.proxies.customers;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import io.eventuate.examples.common.money.Money;


public class MoneyModule extends SimpleModule {

  class MoneyDeserializer extends StdScalarDeserializer<Money> {

    protected MoneyDeserializer() {
      super(Money.class);
    }

    @Override
    public Money deserialize(JsonParser jp, DeserializationContext ctxt) {
      JsonToken token = jp.currentToken();
      if (token == JsonToken.VALUE_STRING) {
        String str = jp.getString().trim();
        if (str.isEmpty())
          return null;
        else
          return new Money(str);
      } else
        return (Money) ctxt.handleUnexpectedToken(getValueType(), jp);
    }
  }

  class MoneySerializer extends StdScalarSerializer<Money> {
    public MoneySerializer() {
      super(Money.class);
    }

    @Override
    public void serialize(Money value, JsonGenerator jgen, SerializationContext provider) {
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