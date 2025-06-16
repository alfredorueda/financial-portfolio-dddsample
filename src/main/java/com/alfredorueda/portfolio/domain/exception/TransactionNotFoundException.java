package com.alfredorueda.portfolio.domain.exception;

public class TransactionNotFoundException extends DomainException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
