package com.jje.point.domain.point.event;

import com.jje.point.domain.point.service.PointService;
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
public class PointEventListener {

    private final PointService pointService;
    private final PointEventPublisher eventPublisher;

    /**
     * 주문 취소 이벤트 수신 → 포인트 복원.
     */
    @KafkaListener(topics = "order-cancelled")
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
