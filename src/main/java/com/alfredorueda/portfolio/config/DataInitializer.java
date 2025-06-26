package com.alfredorueda.portfolio.config;

import com.alfredorueda.portfolio.application.port.in.PortfolioManagementUseCase;
import com.alfredorueda.portfolio.application.port.in.StockTradingUseCase;
import com.alfredorueda.portfolio.domain.Portfolio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("dev")
    public CommandLineRunner initializeData(
            PortfolioManagementUseCase portfolioManagementUseCase,
            StockTradingUseCase stockTradingUseCase) {
        return args -> {
            // Create a demo portfolio
            Portfolio portfolio = portfolioManagementUseCase.createPortfolio("Demo User");
            String portfolioId = portfolio.getId();
            
            // Add initial balance
            portfolioManagementUseCase.deposit(portfolioId, new BigDecimal("10000.00"));
            
            // Buy some stocks
            stockTradingUseCase.buyStock(portfolioId, "AAPL", 10);
            stockTradingUseCase.buyStock(portfolioId, "MSFT", 5);
            stockTradingUseCase.buyStock(portfolioId, "GOOGL", 2);
            
            System.out.println("Demo portfolio initialized with ID: " + portfolioId);
        };
    }
}