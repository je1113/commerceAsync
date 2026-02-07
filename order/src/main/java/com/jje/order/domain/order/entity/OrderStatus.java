package com.jje.order.domain.order.entity;

import java.util.Set;

public enum OrderStatus {

    PENDING(Set.of()),                                      // 주문 생성
    PAYMENT_REQUESTED(Set.of("PENDING")),                   // 결제 요청됨
    PAID(Set.of("PAYMENT_REQUESTED")),                      // 결제 완료
    PAYMENT_FAILED(Set.of("PAYMENT_REQUESTED")),            // 결제 실패
    COMPLETED(Set.of("PAID")),                              // 주문 완료
    CANCELLED(Set.of("PENDING", "PAYMENT_FAILED", "PAID")); // 주문 취소

    private final Set<String> allowedPreviousStatuses;

    OrderStatus(Set<String> allowedPreviousStatuses) {
        this.allowedPreviousStatuses = allowedPreviousStatuses;
    }

    public boolean canTransitionFrom(OrderStatus from) {
        return allowedPreviousStatuses.contains(from.name());
    }
}
