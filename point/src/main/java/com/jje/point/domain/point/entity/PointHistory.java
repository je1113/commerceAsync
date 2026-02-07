package com.jje.point.domain.point.entity;

import com.jje.point.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointTransactionType type;

    private String description;

    private Long orderId;

    @Builder
    public PointHistory(Long userId, BigDecimal amount, PointTransactionType type,
                        String description, Long orderId) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.orderId = orderId;
    }
}
