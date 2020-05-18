package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.web;

import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.Customer;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.CustomerRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.service.CustomerService;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.webapi.CreateCustomerRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.webapi.CreateCustomerResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.webapi.GetCustomerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {

  private CustomerService customerService;
  private CustomerRepository customerRepository;

  @Autowired
  public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
    this.customerService = customerService;
    this.customerRepository = customerRepository;
  }

  @RequestMapping(value = "/customers", method = RequestMethod.POST)
  public CreateCustomerResponse createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
    Customer customer = customerService.createCustomer(createCustomerRequest.getName(), createCustomerRequest.getCreditLimit());
    return new CreateCustomerResponse(customer.getId());
  }

  @RequestMapping(value="/customers/{customerId}", method= RequestMethod.GET)
  public ResponseEntity<GetCustomerResponse> getCustomer(@PathVariable Long customerId) {
    return customerRepository
            .findById(customerId)
            .map(c -> new ResponseEntity<>(new GetCustomerResponse(c.getId(), c.getName(), c.getCreditLimit()), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
