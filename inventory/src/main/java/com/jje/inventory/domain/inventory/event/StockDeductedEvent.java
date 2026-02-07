package com.jje.inventory.domain.inventory.event;

import lombok.Builder;
import lombok.Getter;

/**
 * 재고 차감 이벤트.
 * 결제 완료 후 재고가 차감되었음을 알린다.
 */
@Getter
@Builder
public class StockDeductedEvent {

    private Long inventoryId;
    private Long productId;
    private int quantity;
    private Long orderId;
}
