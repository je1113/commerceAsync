package com.jje.inventory.domain.inventory.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Payment 서비스의 PaymentApprovedEvent에 대응하는 수신 메시지.
 * MSA에서는 서비스 간 클래스를 직접 공유하지 않으므로,
 * 같은 구조를 각 서비스에서 별도로 정의한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApprovedEventMessage {

    private Long paymentId;
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private BigDecimal amount;
}
