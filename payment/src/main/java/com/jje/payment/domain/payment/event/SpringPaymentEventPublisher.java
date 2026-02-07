package com.jje.payment.domain.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringPaymentEventPublisher implements PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishApproved(PaymentApprovedEvent event) {
        log.info("[EVENT] PaymentApproved 발행 - paymentId={}, orderId={}", event.getPaymentId(), event.getOrderId());
        kafkaTemplate.send("payment-approved", event.getOrderNumber(), event);
    }

    @Override
    public void publishFailed(PaymentFailedEvent event) {
        log.info("[EVENT] PaymentFailed 발행 - paymentId={}, orderId={}, reason={}", event.getPaymentId(), event.getOrderId(), event.getReason());
        kafkaTemplate.send("payment-failed", event.getOrderNumber(), event);
    }
}
