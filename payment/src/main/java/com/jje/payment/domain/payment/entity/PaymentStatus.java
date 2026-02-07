package com.jje.payment.domain.payment.entity;

import java.util.Set;

public enum PaymentStatus {

    REQUESTED(Set.of()),                    // 결제 요청됨
    PROCESSING(Set.of("REQUESTED")),        // 결제 처리중 (PG사 통신 등)
    APPROVED(Set.of("PROCESSING")),         // 결제 승인
    FAILED(Set.of("PROCESSING")),           // 결제 실패
    REFUNDED(Set.of("APPROVED"));           // 환불 완료

    private final Set<String> allowedPreviousStatuses;

    PaymentStatus(Set<String> allowedPreviousStatuses) {
        this.allowedPreviousStatuses = allowedPreviousStatuses;
    }

    public boolean canTransitionFrom(PaymentStatus from) {
        return allowedPreviousStatuses.contains(from.name());
    }
}
