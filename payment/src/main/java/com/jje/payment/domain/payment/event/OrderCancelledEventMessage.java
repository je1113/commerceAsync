package com.jje.payment.domain.payment.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Order 서비스의 OrderCancelledEvent에 대응하는 수신 메시지.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelledEventMessage {

    private Long orderId;
    private String orderNumber;
    private Long userId;
    private BigDecimal refundAmount;
    private BigDecimal restorePoints;
}
