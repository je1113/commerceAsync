package com.jje.payment.domain.payment.service;

import com.jje.payment.common.exception.DuplicatePaymentException;
import com.jje.payment.common.exception.EntityNotFoundException;
import com.jje.payment.common.exception.InvalidPaymentStateException;
import com.jje.payment.domain.payment.dto.PaymentResponse;
import com.jje.payment.domain.payment.entity.Payment;
import com.jje.payment.domain.payment.entity.PaymentMethod;
import com.jje.payment.domain.payment.entity.PaymentStatus;
import com.jje.payment.domain.payment.event.PaymentEventPublisher;
import com.jje.payment.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @Test
    @DisplayName("결제를 정상 처리하고 승인 이벤트를 발행한다")
    void processPayment() {
        // given
        Payment payment = Payment.builder()
                .orderId(1L)
                .orderNumber("order-001")
                .userId(1L)
                .amount(BigDecimal.valueOf(30000))
                .method(PaymentMethod.CARD)
                .build();

        given(paymentRepository.existsByOrderNumber("order-001")).willReturn(false);
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);

        // when
        PaymentResponse response = paymentService.processPayment(
                1L, "order-001", 1L, BigDecimal.valueOf(30000), PaymentMethod.CARD);

        // then
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.APPROVED);
        then(eventPublisher).should().publishApproved(any());
    }

    @Test
    @DisplayName("중복 결제 요청 시 예외가 발생한다")
    void duplicatePayment() {
        given(paymentRepository.existsByOrderNumber("order-001")).willReturn(true);

        assertThatThrownBy(() -> paymentService.processPayment(
                1L, "order-001", 1L, BigDecimal.valueOf(30000), PaymentMethod.CARD))
                .isInstanceOf(DuplicatePaymentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 결제 조회 시 예외가 발생한다")
    void getPaymentNotFound() {
        given(paymentRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("APPROVED 상태의 결제를 환불한다")
    void refundPayment() {
        // given
        Payment payment = Payment.builder()
                .orderId(1L)
                .orderNumber("order-001")
                .userId(1L)
                .amount(BigDecimal.valueOf(30000))
                .method(PaymentMethod.CARD)
                .build();
        // REQUESTED → PROCESSING → APPROVED
        payment.changeStatus(PaymentStatus.PROCESSING);
        payment.approve();

        given(paymentRepository.findByOrderId(1L)).willReturn(Optional.of(payment));

        // when
        PaymentResponse response = paymentService.refund(1L);

        // then
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }

    @Test
    @DisplayName("REQUESTED 상태의 결제는 환불할 수 없다")
    void cannotRefundRequestedPayment() {
        Payment payment = Payment.builder()
                .orderId(1L)
                .orderNumber("order-001")
                .userId(1L)
                .amount(BigDecimal.valueOf(30000))
                .build();

        given(paymentRepository.findByOrderId(1L)).willReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.refund(1L))
                .isInstanceOf(InvalidPaymentStateException.class);
    }
}
