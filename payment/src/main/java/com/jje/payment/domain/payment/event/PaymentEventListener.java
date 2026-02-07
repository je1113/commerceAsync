package com.jje.payment.domain.payment.event;

import com.jje.payment.domain.payment.entity.PaymentMethod;
import com.jje.payment.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Order 서비스 이벤트 수신 리스너.
 * Kafka를 통해 서비스 간 비동기 메시징으로 동작한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;

    /**
     * 주문 생성 이벤트 수신 → 결제 처리 시작.
     */
    @KafkaListener(topics = "order-created")
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
     */
    @KafkaListener(topics = "order-cancelled")
    public void handleOrderCancelled(OrderCancelledEventMessage event) {
        log.info("[LISTEN] OrderCancelled 수신 - orderId={}, orderNumber={}", event.getOrderId(), event.getOrderNumber());
        paymentService.refund(event.getOrderId());
    }
}
