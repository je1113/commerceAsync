package com.jje.point.domain.point.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PointEarnRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotNull(message = "포인트 금액은 필수입니다")
    @Positive(message = "포인트 금액은 양수여야 합니다")
    private BigDecimal amount;

    private String description;
}
