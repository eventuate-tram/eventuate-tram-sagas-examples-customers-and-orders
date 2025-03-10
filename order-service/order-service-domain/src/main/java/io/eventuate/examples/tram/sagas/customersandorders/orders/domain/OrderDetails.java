package io.eventuate.examples.tram.sagas.customersandorders.orders.domain;

import io.eventuate.examples.common.money.Money;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public record OrderDetails(Long customerId, @Embedded Money orderTotal) {

}
