package com.jje.order.domain.order.event;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 주문 생성 이벤트.
 * 결제 서비스가 이 이벤트를 받아 결제를 처리한다.
 */
@Getter
@Builder
public class OrderCreatedEvent {

    private Long orderId;
    private String orderNumber;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal usedPoints;
}
