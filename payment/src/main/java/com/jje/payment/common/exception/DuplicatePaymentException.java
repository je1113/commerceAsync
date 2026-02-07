package com.jje.payment.common.exception;

/**
 * 같은 orderNumber로 결제가 이미 존재할 때 발생.
 * 멱등성 보장: 중복 요청을 에러로 처리하지 않고, 기존 결제를 반환할 수도 있음.
 */
public class DuplicatePaymentException extends RuntimeException {

    public DuplicatePaymentException(String orderNumber) {
        super("이미 결제가 존재합니다. orderNumber=" + orderNumber);
    }
}
