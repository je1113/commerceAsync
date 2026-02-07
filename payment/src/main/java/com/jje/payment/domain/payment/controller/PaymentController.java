package com.jje.payment.domain.payment.controller;

import com.jje.payment.domain.payment.dto.PaymentResponse;
import com.jje.payment.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getByOrderId(orderId));
    }

    @PostMapping("/order/{orderId}/refund")
    public ResponseEntity<PaymentResponse> refund(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.refund(orderId));
    }
}
