package com.jje.inventory.domain.inventory.event;

/**
 * 재고 이벤트 발행 인터페이스.
 * 현재: Spring ApplicationEventPublisher
 * 추후: Kafka Producer로 교체
 */
public interface InventoryEventPublisher {

    void publishStockDeducted(StockDeductedEvent event);

    void publishStockRestored(StockRestoredEvent event);
}
