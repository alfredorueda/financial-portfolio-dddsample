package com.alfredorueda.portfolio.application.port.in;

import com.alfredorueda.portfolio.domain.SellResult;

/**
 * Interface for stock trading operations
 */
public interface StockTradingUseCase {
    void buyStock(String portfolioId, String ticker, int quantity);
    SellResult sellStock(String portfolioId, String ticker, int quantity);
}