package com.alfredorueda.portfolio.config;

import com.alfredorueda.portfolio.application.port.in.PortfolioUseCase;
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
    public CommandLineRunner initializeData(PortfolioUseCase portfolioUseCase) {
        return args -> {
            // Create a demo portfolio
            Portfolio portfolio = portfolioUseCase.createPortfolio("Demo User");
            String portfolioId = portfolio.id();
            
            // Add initial balance
            portfolioUseCase.deposit(portfolioId, new BigDecimal("10000.00"));
            
            // Buy some stocks
            portfolioUseCase.buyStock(portfolioId, "AAPL", 10);
            portfolioUseCase.buyStock(portfolioId, "MSFT", 5);
            portfolioUseCase.buyStock(portfolioId, "GOOGL", 2);
            
            System.out.println("Demo portfolio initialized with ID: " + portfolioId);
        };
    }
}