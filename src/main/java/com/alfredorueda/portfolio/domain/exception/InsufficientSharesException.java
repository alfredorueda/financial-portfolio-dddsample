package com.alfredorueda.portfolio.domain.exception;

/**
 * InsufficientSharesException indicates that a sell operation cannot be completed
 * because the portfolio does not contain enough shares of the specified stock.
 * 
 * This domain exception is thrown when:
 * - An attempt is made to sell more shares than are available in the portfolio
 * - The requested quantity exceeds the total remaining shares across all lots
 * 
 * It enforces the business rule that investors cannot sell more shares than they own,
 * preventing "short selling" within this simplified portfolio management system.
 */
public class InsufficientSharesException extends DomainException {
    public InsufficientSharesException(String message) {
        super(message);
    }

    public InsufficientSharesException(String symbol, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient shares for stock %s: requested %d but only %d available", 
                           symbol, requestedQuantity, availableQuantity));
    }
}