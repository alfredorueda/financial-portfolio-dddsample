package com.alfredorueda.portfolio.application.port.in;

import com.alfredorueda.portfolio.domain.InvestmentSummaryDto;
import com.alfredorueda.portfolio.domain.Portfolio;
import com.alfredorueda.portfolio.domain.SellResult;
import com.alfredorueda.portfolio.domain.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PortfolioUseCase {
    Portfolio createPortfolio(String ownerName);
    Portfolio getPortfolio(String id);
    void deposit(String portfolioId, BigDecimal amount);
    void withdraw(String portfolioId, BigDecimal amount);
    void buyStock(String portfolioId, String ticker, int quantity);
    SellResult sellStock(String portfolioId, String ticker, int quantity);
    List<Transaction> getTransactions(String portfolioId, Optional<String> ticker, 
                                      Optional<String> type, Optional<LocalDate> fromDate,
                                      Optional<LocalDate> toDate, Optional<BigDecimal> minAmount,
                                      Optional<BigDecimal> maxAmount);
    List<InvestmentSummaryDto> getPortfolioPerformance(String portfolioId, Optional<Integer> limit);
}
