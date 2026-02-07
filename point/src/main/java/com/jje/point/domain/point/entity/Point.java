package com.jje.point.domain.point.entity;

import com.jje.point.common.entity.BaseEntity;
import com.jje.point.common.exception.InsufficientPointException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal balance;

    @Builder
    public Point(Long userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    public void earn(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void use(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientPointException(this.balance, amount);
        }
        this.balance = this.balance.subtract(amount);
    }

    public void restore(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
