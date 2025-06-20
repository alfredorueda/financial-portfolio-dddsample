package com.alfredorueda.portfolio.application.port.in;

import com.alfredorueda.portfolio.domain.InvestmentSummaryDto;
import com.alfredorueda.portfolio.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface for portfolio analysis and reporting operations
 */
public interface PortfolioAnalysisUseCase {
    List<Transaction> getTransactions(String portfolioId, Optional<String> ticker,
                                      Optional<String> type, Optional<LocalDate> fromDate,
                                      Optional<LocalDate> toDate, Optional<BigDecimal> minAmount,
                                      Optional<BigDecimal> maxAmount);
    List<InvestmentSummaryDto> getPortfolioPerformance(String portfolioId, Optional<Integer> limit);
}