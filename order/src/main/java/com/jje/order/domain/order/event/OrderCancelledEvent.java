package com.jje.order.domain.order.event;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 주문 취소 이벤트.
 * 결제 환불, 재고 복구, 포인트 환불 등 보상 트랜잭션을 트리거한다.
 */
@Getter
@Builder
public class OrderCancelledEvent {

    private Long orderId;
    private String orderNumber;
    private Long userId;
    private BigDecimal refundAmount;
    private BigDecimal restorePoints;
}
