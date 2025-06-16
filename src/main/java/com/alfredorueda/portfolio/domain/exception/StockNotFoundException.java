package com.alfredorueda.portfolio.domain.exception;

public class StockNotFoundException extends DomainException {
    public StockNotFoundException(String message) {
        super(message);
    }
}
