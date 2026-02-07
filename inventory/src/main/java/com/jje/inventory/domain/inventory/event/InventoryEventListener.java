package com.jje.inventory.domain.inventory.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Payment / Order 서비스 이벤트 수신 리스너.
 *
 * 현재: Spring @EventListener (같은 JVM 내에서만 동작)
 * 추후: @KafkaListener로 교체 (서비스 간 통신)
 *
 * 지금은 다른 서비스와 같은 JVM에서 돌리지 않으므로,
 * 이 리스너는 구조 예시용이다. Kafka 도입 시 실제로 동작한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    /**
     * 결제 승인 이벤트 수신 → 재고 차감 처리.
     * Kafka 도입 시: @KafkaListener(topics = "payment-approved")
     */
    @EventListener
    public void handlePaymentApproved(PaymentApprovedEventMessage event) {
        log.info("[LISTEN] PaymentApproved 수신 - paymentId={}, orderId={}, orderNumber={}",
                event.getPaymentId(), event.getOrderId(), event.getOrderNumber());
        // TODO: Kafka 도입 시 InventoryService를 호출하여 재고 차감 처리
    }

    /**
     * 주문 취소 이벤트 수신 → 재고 복구 처리.
     * Kafka 도입 시: @KafkaListener(topics = "order-cancelled")
     */
    @EventListener
    public void handleOrderCancelled(OrderCancelledEventMessage event) {
        log.info("[LISTEN] OrderCancelled 수신 - orderId={}, orderNumber={}",
                event.getOrderId(), event.getOrderNumber());
        // TODO: Kafka 도입 시 InventoryService를 호출하여 재고 복구 처리
    }
}
