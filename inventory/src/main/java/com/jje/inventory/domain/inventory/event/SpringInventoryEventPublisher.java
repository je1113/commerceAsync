package com.jje.inventory.domain.inventory.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringInventoryEventPublisher implements InventoryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishStockDeducted(StockDeductedEvent event) {
        log.info("[EVENT] StockDeducted 발행 - inventoryId={}, productId={}, quantity={}",
                event.getInventoryId(), event.getProductId(), event.getQuantity());
        kafkaTemplate.send("stock-deducted", String.valueOf(event.getOrderId()), event);
    }

    @Override
    public void publishStockRestored(StockRestoredEvent event) {
        log.info("[EVENT] StockRestored 발행 - inventoryId={}, productId={}, quantity={}",
                event.getInventoryId(), event.getProductId(), event.getQuantity());
        kafkaTemplate.send("stock-restored", String.valueOf(event.getOrderId()), event);
    }
}
