package com.jje.inventory.domain.inventory.event;

import lombok.Builder;
import lombok.Getter;

/**
 * 재고 복구 이벤트.
 * 주문 취소 시 재고가 복구되었음을 알린다.
 */
@Getter
@Builder
public class StockRestoredEvent {

    private Long inventoryId;
    private Long productId;
    private int quantity;
    private Long orderId;
}
