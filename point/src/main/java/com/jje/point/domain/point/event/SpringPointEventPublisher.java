package com.jje.point.domain.point.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringPointEventPublisher implements PointEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishRestored(PointRestoredEvent event) {
        log.info("[EVENT] PointRestored 발행 - userId={}, amount={}, orderId={}",
                event.getUserId(), event.getAmount(), event.getOrderId());
        kafkaTemplate.send("point-restored", String.valueOf(event.getOrderId()), event);
    }
}
