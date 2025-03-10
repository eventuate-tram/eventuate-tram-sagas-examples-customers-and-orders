package io.eventuate.examples.tram.sagas.customersandorders.customers.web;

import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.Customer;
import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerRepository;
import io.eventuate.examples.tram.sagas.customersandorders.customers.domain.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class CustomerController {

  private final CustomerService customerService;
  private final CustomerRepository customerRepository;

  public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
    this.customerService = customerService;
    this.customerRepository = customerRepository;
  }

  @PostMapping("/customers")
  public CreateCustomerResponse createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
    Customer customer = customerService.createCustomer(createCustomerRequest.name(), createCustomerRequest.creditLimit());
    return new CreateCustomerResponse(customer.getId());
  }

  @GetMapping("/customers")
  public ResponseEntity<GetCustomersResponse> getAll() {
    return ResponseEntity.ok(new GetCustomersResponse(StreamSupport.stream(customerRepository.findAll().spliterator(), false)
            .map(c -> new GetCustomerResponse(c.getId(), c.getName(), c.getCreditLimit())).collect(Collectors.toList())));
  }

  @GetMapping("/customers/{customerId}")
  public ResponseEntity<GetCustomerResponse> getCustomer(@PathVariable Long customerId) {
    return customerRepository
            .findById(customerId)
            .map(c -> new ResponseEntity<>(new GetCustomerResponse(c.getId(), c.getName(), c.getCreditLimit()), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
