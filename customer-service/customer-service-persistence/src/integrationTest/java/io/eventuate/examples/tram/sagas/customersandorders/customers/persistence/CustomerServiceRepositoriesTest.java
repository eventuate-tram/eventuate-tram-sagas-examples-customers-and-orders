package io.eventuate.examples.tram.sagas.customersandorders.customers.persistence;

import io.eventuate.common.testcontainers.ContainerTestUtil;
import io.eventuate.common.testcontainers.DatabaseContainerFactory;
import io.eventuate.common.testcontainers.EventuateDatabaseContainer;
import io.eventuate.common.testcontainers.PropertyProvidingContainer;
import io.eventuate.examples.common.money.Money;
import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.Customer;
import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ContextConfiguration(classes= CustomerServiceRepositoriesTest.Config.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NEVER)
public class CustomerServiceRepositoriesTest {


  public static EventuateDatabaseContainer<?> database =
      DatabaseContainerFactory.makeVanillaDatabaseContainer()
          .withReuse(ContainerTestUtil.shouldReuse());

  @DynamicPropertySource
  static void registerMySqlProperties(DynamicPropertyRegistry registry) {
    PropertyProvidingContainer.startAndProvideProperties(registry, database);
  }

  public static final String customerName = "Chris";

  @Configuration
  @Import(CustomerPersistenceConfiguration.class)
  static public class Config {
  }

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private TransactionTemplate transactionTemplate;

  @Test
  public void shouldSaveAndLoadCustomer() {
    Money creditLimit = new Money("12.34");
    Money amount = new Money("10");
    Money expectedAvailableCredit = creditLimit.subtract(amount);

    Customer c = new Customer(customerName, creditLimit);

    transactionTemplate.executeWithoutResult( ts -> customerRepository.save(c) );

    transactionTemplate.executeWithoutResult(ts -> {
      Customer c2 = customerRepository.findById(c.getId()).get();
      assertEquals(customerName, c2.getName());
      assertEquals(creditLimit, c2.getCreditLimit());
      assertEquals(creditLimit, c2.availableCredit());

      c2.reserveCredit(1234L, amount);
    });

    transactionTemplate.executeWithoutResult(ts -> {
      Customer c2 = customerRepository.findById(c.getId()).get();
      assertEquals(expectedAvailableCredit, c2.availableCredit());

    });


  }
}
