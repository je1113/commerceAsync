package com.jje.payment.domain.payment.event;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 결제 승인 이벤트.
 * Order 서비스가 이 이벤트를 받아 주문 상태를 PAID로 변경한다.
 */
@Getter
@Builder
public class PaymentApprovedEvent {

    private Long paymentId;
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private BigDecimal amount;
}
