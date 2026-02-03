package com.pms.service;

import com.pms.entity.Payment;
import com.pms.entity.Policy;
import com.pms.entity.Customer;
import com.pms.repository.PaymentRepository;
import com.pms.repository.CustomerRepository;
import com.pms.repository.PolicyRepository;
import jakarta.persistence.PrePersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PolicyRepository policyRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @PrePersist
    public String generatePaymentId() {
        long count = paymentRepository.count() + 1;
        return String.format("PAY%03d", count);
    }

    @Transactional
    public Payment createPayment(String paymentId, String customerId, String policyId, Double amount, Payment.PaymentType paymentType) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        Optional<Policy> policyOpt = policyRepository.findByPolicyId(policyId);
        if (customerOpt.isPresent() && policyOpt.isPresent()) {
            Payment payment = new Payment();
            payment.setPaymentId(generatePaymentId());
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDate.now());
            payment.setPaymentType(paymentType);
            payment.setCustomer(customerOpt.get());
            payment.setPolicy(policyOpt.get());
            System.out.println("PAYMENT SAVED FOR CUSTOMER = " + customerOpt.get().getId());
            return paymentRepository.save(payment);
        } else {
            throw new RuntimeException("Customer or Policy not found");
        }
    }

    @PostMapping("/addPayment")



    public void deletePayment(String paymentId) {
        paymentRepository.deleteById(paymentId);
    }

//    public List<Payment> viewCustPayments(String customerId) {
//        List<Payment> payments = paymentRepository.findByCustomer_Id(customerId);
//        // Optionally ensure that payment's policy is set if needed
//        return payments;
//    }
public List<Payment> viewCustPayments(String customerId) {
    List<Payment> payments = paymentRepository.findByCustomer_Id(customerId);
    // Optionally ensure that payment's policy is set if needed
    return payments;
}
}
