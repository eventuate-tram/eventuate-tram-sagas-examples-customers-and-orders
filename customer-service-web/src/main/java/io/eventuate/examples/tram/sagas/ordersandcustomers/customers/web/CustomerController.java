package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.web;

import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.CreateCustomerRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.CreateCustomerResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.GetCustomerResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.web.GetCustomersResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.Customer;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.CustomerRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

  @RequestMapping(value="/customers", method= RequestMethod.GET)
  public ResponseEntity<GetCustomersResponse> getAll() {
    return ResponseEntity.ok(new GetCustomersResponse(StreamSupport.stream(customerRepository.findAll().spliterator(), false)
            .map(c -> new GetCustomerResponse(c.getId(), c.getName(), c.getCreditLimit())).collect(Collectors.toList())));
  }

  @RequestMapping(value="/customers/{customerId}", method= RequestMethod.GET)
  public ResponseEntity<GetCustomerResponse> getCustomer(@PathVariable Long customerId) {
    return customerRepository
            .findById(customerId)
            .map(c -> new ResponseEntity<>(new GetCustomerResponse(c.getId(), c.getName(), c.getCreditLimit()), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
