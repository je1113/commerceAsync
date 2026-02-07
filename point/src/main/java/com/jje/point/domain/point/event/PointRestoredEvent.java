package com.jje.point.domain.point.event;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 포인트 복원 완료 이벤트.
 */
@Getter
@Builder
public class PointRestoredEvent {

    private Long userId;
    private BigDecimal amount;
    private Long orderId;
}
