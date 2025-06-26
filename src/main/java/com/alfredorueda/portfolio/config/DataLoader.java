package com.alfredorueda.portfolio.config;

import com.alfredorueda.portfolio.application.port.in.PortfolioManagementUseCase;
import com.alfredorueda.portfolio.application.port.in.StockPriceUseCase;
import com.alfredorueda.portfolio.application.port.in.StockTradingUseCase;
import com.alfredorueda.portfolio.domain.Portfolio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
public class DataLoader {
    
    @Bean
    @Profile("!test & !integrationtest") // Skip in test environments
    public CommandLineRunner initData(
            PortfolioManagementUseCase portfolioManagementUseCase, 
            StockTradingUseCase stockTradingUseCase,
            StockPriceUseCase stockPriceUseCase) {
        
        return args -> {
            // Create a demo portfolio
            Portfolio demoPortfolio = portfolioManagementUseCase.createPortfolio("Demo User");
            String portfolioId = demoPortfolio.getId();
            
            // Add initial deposit - increased to handle GOOGL purchase
            portfolioManagementUseCase.deposit(portfolioId, new BigDecimal("25000.00"));
            
            // Buy some stocks
            stockTradingUseCase.buyStock(portfolioId, "AAPL", 10);
            stockTradingUseCase.buyStock(portfolioId, "MSFT", 5);
            stockTradingUseCase.buyStock(portfolioId, "GOOGL", 8);
            
            // Sell some shares
            stockTradingUseCase.sellStock(portfolioId, "AAPL", 3);
            
            // Add another deposit and buy more
            portfolioManagementUseCase.deposit(portfolioId, new BigDecimal("5000.00"));
            stockTradingUseCase.buyStock(portfolioId, "AMZN", 4);
            
            System.out.println("Demo portfolio created with ID: " + portfolioId);
        };
    }
    
    @Bean
    @Profile("test | integrationtest") // Only used in test environments
    public CommandLineRunner initTestData(PortfolioManagementUseCase portfolioManagementUseCase) {
        return args -> {
            // Create a test portfolio with just a deposit - no stock purchases
            Portfolio testPortfolio = portfolioManagementUseCase.createPortfolio("Test User");
            String portfolioId = testPortfolio.getId();
            
            // Add initial deposit for tests
            portfolioManagementUseCase.deposit(portfolioId, new BigDecimal("50000.00"));
            
            System.out.println("Test portfolio created with ID: " + portfolioId);
        };
    }
}
