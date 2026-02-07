package com.jje.payment.domain.payment.event;

import lombok.Builder;
import lombok.Getter;

/**
 * 결제 실패 이벤트.
 * Order 서비스가 이 이벤트를 받아 주문 상태를 PAYMENT_FAILED로 변경한다.
 */
@Getter
@Builder
public class PaymentFailedEvent {

    private Long paymentId;
    private Long orderId;
    private String orderNumber;
    private String reason;
}
