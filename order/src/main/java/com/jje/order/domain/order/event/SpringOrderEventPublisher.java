package com.jje.order.domain.order.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Spring ApplicationEvent 기반 이벤트 발행 구현체.
 * 추후 Kafka 구현체(KafkaOrderEventPublisher)로 교체 시
 * 이 클래스만 바꾸면 된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringOrderEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("[EVENT] OrderCreated 발행 - orderId={}, orderNumber={}", event.getOrderId(), event.getOrderNumber());
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishOrderCancelled(OrderCancelledEvent event) {
        log.info("[EVENT] OrderCancelled 발행 - orderId={}, orderNumber={}", event.getOrderId(), event.getOrderNumber());
        applicationEventPublisher.publishEvent(event);
    }
}
