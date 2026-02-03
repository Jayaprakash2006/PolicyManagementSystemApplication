package com.pms.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pms.entity.Customer;
import com.pms.exception.InvalidEntityException;
import com.pms.service.CustomerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "http://localhost:8031")
public class CustomerController {

    @Autowired
    CustomerService service;

    @PostMapping("/register")
    public ResponseEntity<String> addCustomer(@Valid @RequestBody Customer cust) {
        String response = service.addCustomer(cust);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Customer> login(@RequestBody Customer cust) throws InvalidEntityException {
         Customer loggedInCustomer = service.login(cust.getEmail(), cust.getPassword());
         return ResponseEntity.ok(loggedInCustomer);
    }
    
    @PostMapping("/updateCustomer")
    public Customer updateCustomer(@RequestBody Customer cust) throws InvalidEntityException {
        return service.updateCustomer(cust.getId(), cust);
    }
    
    @PostMapping("/deleteCustomer")
    public String deleteCustomer(@RequestBody Customer cust) throws InvalidEntityException {
        return service.deleteCustomer(cust);
    }
    
    @PostMapping("/changePassword")
    public Customer changePassword(@RequestBody Customer cust) throws InvalidEntityException {
        return service.changeCustomerPassword(cust.getId(), cust);
    }
}
