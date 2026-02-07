package com.jje.payment.domain.payment.event;

import com.jje.payment.domain.payment.entity.PaymentMethod;
import com.jje.payment.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Order 서비스 이벤트 수신 리스너.
 *
 * 현재: Spring @EventListener (같은 JVM 내에서만 동작)
 * 추후: @KafkaListener로 교체 (서비스 간 통신)
 *
 * 지금은 Order 서비스와 같은 JVM에서 돌리지 않으므로,
 * 이 리스너는 구조 예시용이다. Kafka 도입 시 실제로 동작한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;

    /**
     * 주문 생성 이벤트 수신 → 결제 처리 시작.
     * Kafka 도입 시: @KafkaListener(topics = "order-created")
     */
    @EventListener
    public void handleOrderCreated(OrderCreatedEventMessage event) {
        log.info("[LISTEN] OrderCreated 수신 - orderId={}, orderNumber={}", event.getOrderId(), event.getOrderNumber());
        paymentService.processPayment(
                event.getOrderId(),
                event.getOrderNumber(),
                event.getUserId(),
                event.getTotalAmount(),
                PaymentMethod.CARD
        );
    }

    /**
     * 주문 취소 이벤트 수신 → 환불 처리.
     * Kafka 도입 시: @KafkaListener(topics = "order-cancelled")
     */
    @EventListener
    public void handleOrderCancelled(OrderCancelledEventMessage event) {
        log.info("[LISTEN] OrderCancelled 수신 - orderId={}, orderNumber={}", event.getOrderId(), event.getOrderNumber());
        paymentService.refund(event.getOrderId());
    }
}
