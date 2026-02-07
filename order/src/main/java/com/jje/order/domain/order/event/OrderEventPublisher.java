package com.jje.order.domain.order.event;

/**
 * 주문 이벤트 발행 인터페이스.
 * 현재는 Spring ApplicationEventPublisher로 구현.
 * 추후 Kafka/RabbitMQ 구현체로 교체 가능.
 */
public interface OrderEventPublisher {

    void publishOrderCreated(OrderCreatedEvent event);

    void publishOrderCancelled(OrderCancelledEvent event);
}
