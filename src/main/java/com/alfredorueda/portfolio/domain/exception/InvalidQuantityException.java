package com.alfredorueda.portfolio.domain.exception;

/**
 * InvalidQuantityException indicates that a stock transaction specifies an invalid number of shares.
 * 
 * This domain exception is thrown when:
 * - A negative or zero quantity is provided for a purchase or sale
 * - An attempt is made to sell more shares than are available in a lot
 * 
 * It enforces the business rule that all stock transactions must involve positive
 * quantities and that investors cannot sell more shares than they own.
 */
public class InvalidQuantityException extends DomainException {
    public InvalidQuantityException(String message) {
        super(message);
    }
}
