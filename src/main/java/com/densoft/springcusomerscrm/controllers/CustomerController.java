package com.densoft.springcusomerscrm.controllers;

import com.densoft.springcusomerscrm.exceptions.CustomerNotFoundException;
import com.densoft.springcusomerscrm.model.Customer;
import com.densoft.springcusomerscrm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customers")
    public List<Customer> getCustomers() {
        return customerService.customers();
    }

    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody Customer customer) {
        customer.setId(0);
        customerService.saveCustomer(customer);
        return customer;
    }

    @GetMapping("/customers/{customerId}")
    public Customer getCustomer(@PathVariable int customerId) {
        Customer customer = customerService.getCustomer(customerId);

        if (customer == null) {
            throw new CustomerNotFoundException("customer with id: " + customerId + " not found");
        }
        return customer;
    }

    @PutMapping("/customers/{customerId}")
    public void updateCustomer(@PathVariable int customerId, @RequestBody Customer customer) {
        customerService.saveCustomer(customer);

    }

    @DeleteMapping("/customers/{customerId}")
    public void deleteCustomer(@PathVariable int customerId) {

        customerService.deleteCustomer(customerId);
    }


}
