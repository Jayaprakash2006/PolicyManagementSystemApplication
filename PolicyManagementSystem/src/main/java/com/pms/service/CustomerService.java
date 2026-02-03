package com.pms.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pms.entity.Customer;
import com.pms.exception.InvalidEntityException;
import com.pms.repository.CustomerRepository;
import jakarta.persistence.PrePersist;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository repo;
    
    @Autowired
    EmailService emailService;  

    @PrePersist
    public String generateId() {
        long count = repo.count() + 1;
        return "C" + String.format("%03d", count); // e.g., C001
    }
    
    public String addCustomer(Customer cust) {
        Optional<Customer> existingCustomer = repo.findByEmail(cust.getEmail());
        if (existingCustomer.isPresent()) {
            return "Customer already exists";
        }
        cust.setId(generateId());
        cust.setVerified(false);
        cust.setActive(true);
        cust.setRegDate(LocalDate.now().toString());
        repo.save(cust);

        String subject = "Welcome to Policy Trust!";
        String body = "Dear " + cust.getName() + ",\n\nYour registration is successful. Thank you for joining us!\n\nYou will be notified once verified by Admin.";
        try {
            emailService.sendEmail(cust.getEmail(), subject, body);
        } catch (Exception e) {
            System.out.println("⚠ Email failed, but customer registered");
        }
        System.out.println("Mail sent");
        return cust.getId();
    }
    
    public Customer login(String email, String password) throws InvalidEntityException {
        Customer customer = repo.findByEmail(email).orElse(null);
        if (customer == null) {
            throw new InvalidEntityException("User doesn't exist. Register your account.");
        }
        if (!customer.getActive()) {
            throw new InvalidEntityException("Your account is deleted/rejected. Kindly register with a new email.");
        }
        if (!customer.getVerified()) {
            throw new InvalidEntityException("Your account is not verified.");
        }
        if (!customer.getPassword().equals(password)) {
            throw new InvalidEntityException("Invalid credentials");
        }
        return customer;
    }

    public Customer updateCustomer(String id, Customer cust) throws InvalidEntityException {
        Optional<Customer> existingCustomer = repo.findById(id);
        Customer customer = existingCustomer.get();
        if (!cust.getName().isEmpty()) customer.setName(cust.getName());
        if (cust.getAge() != null) customer.setAge(cust.getAge());
        if (!cust.getPhone().isEmpty()) customer.setPhone(cust.getPhone());
        if (!cust.getAddress().isEmpty()) customer.setAddress(cust.getAddress());
        repo.save(customer);
        
        String subject = "Your Account Has Been Updated";
        String body = "Dear " + customer.getName() + ",\n\nYour account has been successfully updated.\n\nBest regards,\nPolicy Trust";
        emailService.sendEmail(customer.getEmail(), subject, body);
        return customer;
    }

    public String deleteCustomer(Customer cust) throws InvalidEntityException {
        Optional<Customer> existingCustomer = repo.findById(cust.getId());
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            customer.setVerified(false);
            customer.setActive(false);
            repo.save(customer);
            String subject = "Your Account is Deactivated";
            String body = "Dear " + customer.getName() + ",\n\nYour account has been successfully deactivated.\n\nBest regards,\nPolicy Trust";
            emailService.sendEmail(customer.getEmail(), subject, body);
            return "Customer deleted";
        } else {
            return "Customer not found";
        }
    }

    public Customer changeCustomerPassword(String id, Customer cust) {
        Optional<Customer> existingCustomer = repo.findById(id);
        Customer customer = existingCustomer.get();
        if (!cust.getPassword().isEmpty()) customer.setPassword(cust.getPassword());
        repo.save(customer);
        String subject = "Your Password Has Been Changed";
        String body = "Dear " + customer.getName() + ",\n\nYour account password has been successfully updated.\n\nBest regards,\nPolicy Trust";
        emailService.sendEmail(customer.getEmail(), subject, body);
        return customer;
    }
}
