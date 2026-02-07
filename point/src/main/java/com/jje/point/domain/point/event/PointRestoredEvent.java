package com.jje.point.domain.point.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 포인트 복원 완료 이벤트.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRestoredEvent {

    private Long userId;
    private BigDecimal amount;
    private Long orderId;
}
