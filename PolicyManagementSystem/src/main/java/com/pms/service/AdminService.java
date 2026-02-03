package com.pms.service;

import com.pms.entity.Admin;
import com.pms.entity.Customer;
import com.pms.exception.InvalidEntityException;
import com.pms.repository.AdminRepository;
import com.pms.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

	@Autowired
	AdminRepository adminRepo;

	@Autowired
	CustomerRepository custRepo;

	@Autowired
	EmailService emailService;

	public Admin login(String email, String password) throws InvalidEntityException {
		Admin admin = adminRepo.findByEmail(email);

		if (admin == null) {
			throw new InvalidEntityException("User not found");
		}
		if (!admin.getPassword().equals(password)) {
			throw new InvalidEntityException("Invalid credentials");
		}

		return admin;
	}

	public List<Customer> getVerifiedCustomers() {
		return custRepo.findByVerifiedTrueAndActiveTrue();
	}

	public List<Customer> getUnverifiedCustomers() {
		return custRepo.findByVerifiedFalseAndActiveTrue();
	}

	public List<Customer> getDeletedCustomers() {
		return custRepo.findByVerifiedFalseAndActiveFalse();
	}

	public Customer getCustomerById(String id) throws InvalidEntityException {
		return custRepo.findById(id).orElseThrow(() -> new InvalidEntityException("Customer not found"));
	}

	public List<Customer> getCustomersByName(String name) {
		return custRepo.findByNameContainingIgnoreCase(name);
	}

	public List<Customer> getCustomersByAgeRange(int minAge, int maxAge) {
		return custRepo.findByAgeBetween(minAge, maxAge);
	}

	public String deleteCustomer(Customer cust) throws InvalidEntityException {
		Optional<Customer> existingCustomer = custRepo.findById(cust.getId());
		if (existingCustomer.isPresent()) {
			Customer customer = existingCustomer.get();
			customer.setVerified(false);
			customer.setActive(false);
			custRepo.save(customer);
			emailService.sendAccountStatusEmail(cust.getEmail(), cust.getName(), false);
			return "Customer deleted";
		} else {
			return "Customer not found";
		}
	}

//	@Transactional
//	public String acceptCustomer(Customer cust) throws InvalidEntityException {
//		Optional<Customer> existingCustomer = custRepo.findById(cust.getId());
//		if (existingCustomer.isPresent()) {
//			Customer customer = existingCustomer.get();
//			customer.setVerified(true);
//			customer.setActive(true);
//			custRepo.save(customer);
//			emailService.sendAccountStatusEmail(cust.getEmail(), cust.getName(), true);
//			return "Customer accepted";
//		} else {
//			return "Customer not found";
//		}
//	}
@Transactional
public String acceptCustomer(Customer cust) throws InvalidEntityException {

	Optional<Customer> existingCustomer = custRepo.findById(cust.getId());

	if (existingCustomer.isPresent()) {
		Customer customer = existingCustomer.get();
		customer.setVerified(true);
		customer.setActive(true);
		custRepo.save(customer);

		try {
			emailService.sendAccountStatusEmail(
					cust.getEmail(), cust.getName(), true);
		} catch (Exception e) {
			// log only, DO NOT fail request
			System.out.println("Email failed: " + e.getMessage());
		}

		return "Customer accepted";
	} else {
		return "Customer not found";
	}
}


	public String rejectCustomer(Customer cust) throws InvalidEntityException {
		Optional<Customer> existingCustomer = custRepo.findById(cust.getId());
		if (existingCustomer.isPresent()) {
			Customer customer = existingCustomer.get();
			customer.setVerified(false);
			customer.setActive(false);
			custRepo.save(customer);
			emailService.sendAccountStatusEmail(cust.getEmail(), cust.getName(), false);
			return "Customer rejected";
		} else {
			return "Customer not found";
		}
	}
}
