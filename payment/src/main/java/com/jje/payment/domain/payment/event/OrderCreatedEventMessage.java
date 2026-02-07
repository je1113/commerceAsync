package com.jje.payment.domain.payment.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Order 서비스의 OrderCreatedEvent에 대응하는 수신 메시지.
 * MSA에서는 서비스 간 클래스를 직접 공유하지 않으므로,
 * 같은 구조를 각 서비스에서 별도로 정의한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEventMessage {

    private Long orderId;
    private String orderNumber;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal usedPoints;
}
