package com.jje.point.domain.point.event;

import com.jje.point.domain.point.service.PointService;
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
public class PointEventListener {

    private final PointService pointService;
    private final PointEventPublisher eventPublisher;

    /**
     * 주문 취소 이벤트 수신 → 포인트 복원.
     * Kafka 도입 시: @KafkaListener(topics = "order-cancelled")
     */
    @EventListener
    public void handleOrderCancelled(OrderCancelledEventMessage event) {
        log.info("[LISTEN] OrderCancelled 수신 - orderId={}, userId={}, restorePoints={}",
                event.getOrderId(), event.getUserId(), event.getRestorePoints());

        if (event.getRestorePoints() != null && event.getRestorePoints().signum() > 0) {
            pointService.restore(event.getUserId(), event.getRestorePoints(), event.getOrderId());

            eventPublisher.publishRestored(PointRestoredEvent.builder()
                    .userId(event.getUserId())
                    .amount(event.getRestorePoints())
                    .orderId(event.getOrderId())
                    .build());
        }
    }
}
