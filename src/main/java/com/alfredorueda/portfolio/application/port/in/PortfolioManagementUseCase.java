package com.alfredorueda.portfolio.application.port.in;

import com.alfredorueda.portfolio.domain.Portfolio;
import java.math.BigDecimal;

/**
 * Interface for basic portfolio management operations
 */
public interface PortfolioManagementUseCase {
    Portfolio createPortfolio(String ownerName);
    Portfolio getPortfolio(String id);
    void deposit(String portfolioId, BigDecimal amount);
    void withdraw(String portfolioId, BigDecimal amount);
}