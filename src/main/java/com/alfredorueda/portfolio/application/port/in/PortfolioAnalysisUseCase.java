package com.alfredorueda.portfolio.application.port.in;

import com.alfredorueda.portfolio.application.port.in.dto.TransactionFilter;
import com.alfredorueda.portfolio.domain.InvestmentSummaryDto;
import com.alfredorueda.portfolio.domain.Transaction;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for portfolio analysis and reporting operations
 */
public interface PortfolioAnalysisUseCase {
    /**
     * Retrieves transactions for a portfolio with optional filtering criteria
     * @param filter The filter criteria for transactions
     * @return List of transactions matching the filter criteria
     */
    @Transactional(readOnly = true)
    List<Transaction> getTransactions(TransactionFilter filter);
    
    /**
     * Retrieves portfolio performance metrics
     * @param portfolioId The portfolio ID
     * @param limit Optional limit to the number of results
     * @return List of investment summaries
     */
    List<InvestmentSummaryDto> getPortfolioPerformance(String portfolioId, Integer limit);
}