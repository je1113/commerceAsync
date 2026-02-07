package com.jje.inventory.domain.inventory.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringInventoryEventPublisher implements InventoryEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishStockDeducted(StockDeductedEvent event) {
        log.info("[EVENT] StockDeducted 발행 - inventoryId={}, productId={}, quantity={}",
                event.getInventoryId(), event.getProductId(), event.getQuantity());
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishStockRestored(StockRestoredEvent event) {
        log.info("[EVENT] StockRestored 발행 - inventoryId={}, productId={}, quantity={}",
                event.getInventoryId(), event.getProductId(), event.getQuantity());
        applicationEventPublisher.publishEvent(event);
    }
}
