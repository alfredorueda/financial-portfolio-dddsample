package com.alfredorueda.portfolio.domain.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends DomainException {
    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(BigDecimal requested, BigDecimal available) {
        super(String.format("Insufficient balance: requested %s but only %s available", 
                           requested.toString(), available.toString()));
    }
}