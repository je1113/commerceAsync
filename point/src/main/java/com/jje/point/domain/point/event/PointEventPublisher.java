package com.jje.point.domain.point.event;

/**
 * 포인트 이벤트 발행 인터페이스.
 * 현재: Spring ApplicationEventPublisher
 * 추후: Kafka Producer로 교체
 */
public interface PointEventPublisher {

    void publishRestored(PointRestoredEvent event);
}
