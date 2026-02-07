package com.jje.order.domain.order.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringOrderEventPublisher implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("[EVENT] OrderCreated 발행 - orderId={}, orderNumber={}", event.getOrderId(), event.getOrderNumber());
        kafkaTemplate.send("order-created", event.getOrderNumber(), event);
    }

    @Override
    public void publishOrderCancelled(OrderCancelledEvent event) {
        log.info("[EVENT] OrderCancelled 발행 - orderId={}, orderNumber={}", event.getOrderId(), event.getOrderNumber());
        kafkaTemplate.send("order-cancelled", event.getOrderNumber(), event);
    }
}
