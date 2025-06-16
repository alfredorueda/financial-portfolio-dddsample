package com.alfredorueda.portfolio.domain.exception;

/**
 * InvalidAmountException indicates that a financial operation specifies an invalid monetary amount.
 * 
 * This domain exception is thrown when:
 * - A negative or zero amount is provided for deposits
 * - A negative or zero price is provided for stock transactions
 * 
 * It enforces the business rule that all financial values in transactions must be
 * positive numbers, maintaining the integrity of the financial calculations.
 */
public class InvalidAmountException extends DomainException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
