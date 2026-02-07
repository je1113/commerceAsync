package com.jje.point.domain.point.dto;

import com.jje.point.domain.point.entity.PointHistory;
import com.jje.point.domain.point.entity.PointTransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistoryResponse {

    private Long id;
    private Long userId;
    private BigDecimal amount;
    private PointTransactionType type;
    private String description;
    private Long orderId;
    private LocalDateTime createdAt;

    public static PointHistoryResponse from(PointHistory history) {
        return PointHistoryResponse.builder()
                .id(history.getId())
                .userId(history.getUserId())
                .amount(history.getAmount())
                .type(history.getType())
                .description(history.getDescription())
                .orderId(history.getOrderId())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
