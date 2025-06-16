package com.alfredorueda.portfolio.config;

import com.alfredorueda.portfolio.application.port.in.PortfolioUseCase;
import com.alfredorueda.portfolio.domain.Portfolio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {
    
    @Bean
    public CommandLineRunner initData(PortfolioUseCase portfolioUseCase) {
        return args -> {
            // Create a demo portfolio
            Portfolio demoPortfolio = portfolioUseCase.createPortfolio("Demo User");
            String portfolioId = demoPortfolio.getId();
            
            // Add initial deposit
            portfolioUseCase.deposit(portfolioId, new BigDecimal("10000.00"));
            
            // Buy some stocks
            portfolioUseCase.buyStock(portfolioId, "AAPL", 10, new BigDecimal("170.50"));
            portfolioUseCase.buyStock(portfolioId, "MSFT", 5, new BigDecimal("375.20"));
            portfolioUseCase.buyStock(portfolioId, "GOOGL", 8, new BigDecimal("140.80"));
            
            // Sell some shares
            portfolioUseCase.sellStock(portfolioId, "AAPL", 3, new BigDecimal("175.30"));
            
            // Add another deposit and buy more
            portfolioUseCase.deposit(portfolioId, new BigDecimal("5000.00"));
            portfolioUseCase.buyStock(portfolioId, "AMZN", 4, new BigDecimal("176.25"));
            
            System.out.println("Demo portfolio created with ID: " + portfolioId);
        };
    }
}
