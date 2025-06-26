package com.alfredorueda.portfolio.application.port.out;

import com.alfredorueda.portfolio.domain.Transaction;
import com.alfredorueda.portfolio.domain.TransactionType;
import com.alfredorueda.portfolio.application.port.in.dto.TransactionFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionPort {
    Transaction save(Transaction transaction);

    /**
     * Finds transactions for a portfolio based on filtering criteria.
     * @param filter The filter criteria for transactions
     * @return List of transactions matching the filter criteria
     */
    List<Transaction> findByPortfolioId(TransactionFilter filter);
}
