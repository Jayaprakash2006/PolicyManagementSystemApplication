package com.pms.controller;

import com.pms.entity.Customer;
import com.pms.entity.Payment;
import com.pms.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "http://localhost:8031")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

//    @PostMapping("/viewCustPayments")
//    public List<Payment> viewCustPayments(@RequestParam String customerId) {
//        return paymentService.viewCustPayments(customerId);
//    }
@PostMapping("/viewCustPayments")
public List<Payment> viewCustPayments(@RequestParam String customerId) {
    return paymentService.viewCustPayments(customerId);
}


    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {

        Payment createdPayment = paymentService.createPayment(
                payment.getPaymentId(),
                payment.getCustomer().getId(),
                payment.getPolicy().getPolicyId(),
                payment.getAmount(),
                payment.getPaymentType()
        );
        return ResponseEntity.ok(createdPayment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
