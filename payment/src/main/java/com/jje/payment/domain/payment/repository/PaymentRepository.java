package com.jje.payment.domain.payment.repository;

import com.jje.payment.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderNumber(String orderNumber);

    Optional<Payment> findByOrderId(Long orderId);

    boolean existsByOrderNumber(String orderNumber);
}
