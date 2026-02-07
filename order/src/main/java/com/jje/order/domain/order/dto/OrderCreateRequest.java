package com.jje.order.domain.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;

    @NotEmpty(message = "주문 상품은 1개 이상이어야 합니다")
    @Valid
    private List<OrderItemRequest> items;

    @PositiveOrZero(message = "포인트 사용액은 0 이상이어야 합니다")
    private BigDecimal usedPoints;
}
