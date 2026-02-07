package com.jje.point.common.exception;

import java.math.BigDecimal;

public class InsufficientPointException extends RuntimeException {

    public InsufficientPointException(BigDecimal balance, BigDecimal requested) {
        super("포인트가 부족합니다. 보유=" + balance + ", 요청=" + requested);
    }
}
