package com.alfredorueda.portfolio.domain.exception;

/**
 * PortfolioNotFoundException indicates that an operation cannot be completed because
 * the specified portfolio does not exist.
 * 
 * This domain exception is thrown when:
 * - An attempt is made to access or modify a portfolio with an ID that doesn't exist
 * - An operation refers to a portfolio that may have been deleted
 * 
 * It enforces entity integrity at the aggregate root level, ensuring operations
 * are only performed on portfolios that actually exist in the system.
 */
public class PortfolioNotFoundException extends DomainException {
    public PortfolioNotFoundException(String message) {
        super(message);
    }
}
