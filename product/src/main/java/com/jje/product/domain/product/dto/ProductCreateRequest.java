package com.jje.product.domain.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 200, message = "상품명은 200자 이내여야 합니다")
    private String name;

    @Size(max = 2000, message = "상품 설명은 2000자 이내여야 합니다")
    private String description;

    @NotNull(message = "가격은 필수입니다")
    @Positive(message = "가격은 0보다 커야 합니다")
    private BigDecimal price;

    @NotNull(message = "카테고리는 필수입니다")
    private Long categoryId;
}
