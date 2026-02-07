package com.jje.inventory.domain.inventory.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Payment / Order 서비스 이벤트 수신 리스너.
 * Kafka를 통해 서비스 간 비동기 메시징으로 동작한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    /**
     * 결제 승인 이벤트 수신 → 재고 차감 처리.
     * 현재는 주문 상품 정보(productId, quantity)가 이벤트에 포함되지 않으므로 로그만 남긴다.
     * 추후 Order API 호출 또는 이벤트 확장으로 재고 차감 구현 예정.
     */
    @KafkaListener(topics = "payment-approved")
    public void handlePaymentApproved(PaymentApprovedEventMessage event) {
        log.info("[LISTEN] PaymentApproved 수신 - paymentId={}, orderId={}, orderNumber={}",
                event.getPaymentId(), event.getOrderId(), event.getOrderNumber());
        // TODO: 주문 상품 정보 조회 후 InventoryService.deductStock() 호출
    }

    /**
     * 주문 취소 이벤트 수신 → 재고 복구 처리.
     * 현재는 주문 상품 정보(productId, quantity)가 이벤트에 포함되지 않으므로 로그만 남긴다.
     * 추후 Order API 호출 또는 이벤트 확장으로 재고 복구 구현 예정.
     */
    @KafkaListener(topics = "order-cancelled")
    public void handleOrderCancelled(OrderCancelledEventMessage event) {
        log.info("[LISTEN] OrderCancelled 수신 - orderId={}, orderNumber={}",
                event.getOrderId(), event.getOrderNumber());
        // TODO: 주문 상품 정보 조회 후 InventoryService.restoreStock() 호출
    }
}
