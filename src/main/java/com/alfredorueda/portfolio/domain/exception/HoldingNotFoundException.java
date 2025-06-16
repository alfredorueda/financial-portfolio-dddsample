package com.alfredorueda.portfolio.domain.exception;

/**
 * HoldingNotFoundException indicates that an operation cannot be completed because
 * the specified stock holding does not exist in the portfolio.
 * 
 * This domain exception is thrown when:
 * - An attempt is made to sell shares of a stock that is not in the portfolio
 * - An attempt is made to access information about a holding that doesn't exist
 * 
 * It enforces the domain integrity by ensuring operations are only performed on
 * stocks that are actually owned within the portfolio.
 */
public class HoldingNotFoundException extends DomainException {
    public HoldingNotFoundException(String message) {
        super(message);
    }
}
