package com.jje.point.domain.point.dto;

import com.jje.point.domain.point.entity.Point;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PointResponse {

    private Long id;
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PointResponse from(Point point) {
        return PointResponse.builder()
                .id(point.getId())
                .userId(point.getUserId())
                .balance(point.getBalance())
                .createdAt(point.getCreatedAt())
                .updatedAt(point.getUpdatedAt())
                .build();
    }
}
