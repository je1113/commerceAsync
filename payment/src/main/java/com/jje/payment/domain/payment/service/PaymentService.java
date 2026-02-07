package com.jje.payment.domain.payment.service;

import com.jje.payment.common.exception.DuplicatePaymentException;
import com.jje.payment.common.exception.EntityNotFoundException;
import com.jje.payment.common.exception.InvalidPaymentStateException;
import com.jje.payment.domain.payment.dto.PaymentResponse;
import com.jje.payment.domain.payment.entity.Payment;
import com.jje.payment.domain.payment.entity.PaymentMethod;
import com.jje.payment.domain.payment.entity.PaymentStatus;
import com.jje.payment.domain.payment.event.PaymentApprovedEvent;
import com.jje.payment.domain.payment.event.PaymentEventPublisher;
import com.jje.payment.domain.payment.event.PaymentFailedEvent;
import com.jje.payment.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher eventPublisher;

    /**
     * 결제 처리 (이벤트 리스너 또는 직접 호출).
     * 1. 멱등성 체크 (같은 orderNumber 중복 방지)
     * 2. Payment 생성 (REQUESTED)
     * 3. 결제 시도 (PROCESSING → APPROVED or FAILED)
     * 4. 결과 이벤트 발행
     */
    @Transactional
    public PaymentResponse processPayment(Long orderId, String orderNumber,
                                           Long userId, BigDecimal amount,
                                           PaymentMethod method) {
        // 1. 멱등성 체크
        if (paymentRepository.existsByOrderNumber(orderNumber)) {
            throw new DuplicatePaymentException(orderNumber);
        }

        // 2. Payment 생성
        Payment payment = Payment.builder()
                .orderId(orderId)
                .orderNumber(orderNumber)
                .userId(userId)
                .amount(amount)
                .method(method)
                .build();

        payment = paymentRepository.save(payment);

        // 3. 결제 처리 시도
        payment.changeStatus(PaymentStatus.PROCESSING);

        try {
            // 실제로는 여기서 PG사 API를 호출한다
            executePayment(payment);
            payment.approve();

            // 4. 성공 이벤트 발행
            eventPublisher.publishApproved(PaymentApprovedEvent.builder()
                    .paymentId(payment.getId())
                    .orderId(payment.getOrderId())
                    .orderNumber(payment.getOrderNumber())
                    .userId(payment.getUserId())
                    .amount(payment.getAmount())
                    .build());

        } catch (Exception e) {
            payment.fail(e.getMessage());

            // 4. 실패 이벤트 발행
            eventPublisher.publishFailed(PaymentFailedEvent.builder()
                    .paymentId(payment.getId())
                    .orderId(payment.getOrderId())
                    .orderNumber(payment.getOrderNumber())
                    .reason(e.getMessage())
                    .build());
        }

        return PaymentResponse.from(payment);
    }

    public PaymentResponse getById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("결제", id));
        return PaymentResponse.from(payment);
    }

    public PaymentResponse getByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("결제(주문)", orderId));
        return PaymentResponse.from(payment);
    }

    /**
     * 환불 처리 (주문 취소 시).
     */
    @Transactional
    public PaymentResponse refund(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("결제(주문)", orderId));

        try {
            payment.refund();
        } catch (IllegalStateException e) {
            throw new InvalidPaymentStateException(e.getMessage());
        }

        log.info("[REFUND] 환불 완료 - orderId={}, amount={}", orderId, payment.getAmount());
        return PaymentResponse.from(payment);
    }

    /**
     * PG사 결제 실행 (시뮬레이션).
     * 실제로는 외부 API 호출이 여기 들어간다.
     */
    private void executePayment(Payment payment) {
        log.info("[PG] 결제 요청 - orderNumber={}, amount={}, method={}",
                payment.getOrderNumber(), payment.getAmount(), payment.getMethod());
        // 실제 PG사 연동 시 여기서 예외가 발생하면 자동으로 FAILED 처리됨
    }
}
