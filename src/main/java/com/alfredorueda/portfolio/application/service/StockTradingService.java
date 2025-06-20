package com.alfredorueda.portfolio.application.service;

import com.alfredorueda.portfolio.application.port.in.StockTradingUseCase;
import com.alfredorueda.portfolio.application.port.out.LoadPortfolioPort;
import com.alfredorueda.portfolio.application.port.out.SavePortfolioPort;
import com.alfredorueda.portfolio.application.port.out.StockPricePort;
import com.alfredorueda.portfolio.application.port.out.TransactionPort;
import com.alfredorueda.portfolio.domain.Portfolio;
import com.alfredorueda.portfolio.domain.SellResult;
import com.alfredorueda.portfolio.domain.Transaction;
import com.alfredorueda.portfolio.domain.exception.PortfolioNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service responsible for stock trading operations
 */
@Service
public class StockTradingService implements StockTradingUseCase {
    
    private final LoadPortfolioPort loadPortfolioPort;
    private final SavePortfolioPort savePortfolioPort;
    private final TransactionPort transactionPort;
    private final StockPricePort stockPricePort;
    
    public StockTradingService(
            LoadPortfolioPort loadPortfolioPort,
            SavePortfolioPort savePortfolioPort,
            TransactionPort transactionPort,
            StockPricePort stockPricePort) {
        this.loadPortfolioPort = loadPortfolioPort;
        this.savePortfolioPort = savePortfolioPort;
        this.transactionPort = transactionPort;
        this.stockPricePort = stockPricePort;
    }
    
    private Portfolio getPortfolio(String id) {
        return loadPortfolioPort.findById(id)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found with id: " + id));
    }
    
    @Override
    @Transactional
    public void buyStock(String portfolioId, String ticker, int quantity) {
        // Get current market price
        BigDecimal currentPrice = stockPricePort.fetchStockPrice(ticker);
        
        // Update portfolio
        Portfolio portfolio = getPortfolio(portfolioId);
        portfolio.buy(ticker, quantity, currentPrice);
        savePortfolioPort.save(portfolio);
        
        // Record transaction
        Transaction transaction = Transaction.createPurchase(portfolioId, ticker, quantity, currentPrice);
        transactionPort.save(transaction);
    }
    
    @Override
    @Transactional
    public SellResult sellStock(String portfolioId, String ticker, int quantity) {
        // Get current market price
        BigDecimal currentPrice = stockPricePort.fetchStockPrice(ticker);
        
        // Update portfolio
        Portfolio portfolio = getPortfolio(portfolioId);
        SellResult result = portfolio.sell(ticker, quantity, currentPrice);
        savePortfolioPort.save(portfolio);
        
        // Record transaction
        Transaction transaction = Transaction.createSale(
                portfolioId, ticker, quantity, currentPrice, result.proceeds(), result.profit());
        transactionPort.save(transaction);
        
        return result;
    }
}