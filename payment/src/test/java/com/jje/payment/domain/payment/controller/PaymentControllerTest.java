package com.jje.payment.domain.payment.controller;

import com.jje.payment.common.exception.EntityNotFoundException;
import com.jje.payment.domain.payment.dto.PaymentResponse;
import com.jje.payment.domain.payment.entity.PaymentMethod;
import com.jje.payment.domain.payment.entity.PaymentStatus;
import com.jje.payment.domain.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @DisplayName("GET /api/payments/{id} - 결제 조회 성공")
    void getPayment() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .orderId(1L)
                .orderNumber("order-001")
                .userId(1L)
                .amount(BigDecimal.valueOf(30000))
                .method(PaymentMethod.CARD)
                .status(PaymentStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(paymentService.getById(1L)).willReturn(response);

        mockMvc.perform(get("/api/payments/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.amount").value(30000));
    }

    @Test
    @DisplayName("GET /api/payments/{id} - 존재하지 않는 결제 조회 시 404")
    void getPaymentNotFound() throws Exception {
        given(paymentService.getById(999L))
                .willThrow(new EntityNotFoundException("결제", 999L));

        mockMvc.perform(get("/api/payments/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/payments/order/{orderId}/refund - 환불 성공")
    void refundPayment() throws Exception {
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .orderId(1L)
                .orderNumber("order-001")
                .userId(1L)
                .amount(BigDecimal.valueOf(30000))
                .method(PaymentMethod.CARD)
                .status(PaymentStatus.REFUNDED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(paymentService.refund(1L)).willReturn(response);

        mockMvc.perform(post("/api/payments/order/1/refund"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));
    }
}
