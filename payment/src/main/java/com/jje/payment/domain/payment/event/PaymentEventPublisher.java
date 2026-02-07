package com.jje.payment.domain.payment.event;

/**
 * 결제 결과 이벤트 발행 인터페이스.
 * 현재: Spring ApplicationEventPublisher
 * 추후: Kafka Producer로 교체
 */
public interface PaymentEventPublisher {

    void publishApproved(PaymentApprovedEvent event);

    void publishFailed(PaymentFailedEvent event);
}
