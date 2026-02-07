package com.jje.payment.domain.payment.entity;

import com.jje.payment.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String failureReason;

    @Builder
    public Payment(Long orderId, String orderNumber, Long userId,
                   BigDecimal amount, PaymentMethod method) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.amount = amount;
        this.method = method != null ? method : PaymentMethod.CARD;
        this.status = PaymentStatus.REQUESTED;
    }

    public void changeStatus(PaymentStatus newStatus) {
        if (!newStatus.canTransitionFrom(this.status)) {
            throw new IllegalStateException(
                    "결제 상태를 " + this.status + "에서 " + newStatus + "(으)로 변경할 수 없습니다");
        }
        this.status = newStatus;
    }

    public void fail(String reason) {
        changeStatus(PaymentStatus.FAILED);
        this.failureReason = reason;
    }

    public void approve() {
        changeStatus(PaymentStatus.APPROVED);
    }

    public void refund() {
        changeStatus(PaymentStatus.REFUNDED);
    }
}
