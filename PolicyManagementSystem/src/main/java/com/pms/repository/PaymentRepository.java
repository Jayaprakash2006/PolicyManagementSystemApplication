package com.pms.repository;

import com.pms.entity.Customer;
import com.pms.entity.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
//    List<Payment> findByCustomerId(String id);
    List<Payment> findByCustomer_Id(String customerId);

//List<Payment> findByCustomer_Id(String customerId);


}
