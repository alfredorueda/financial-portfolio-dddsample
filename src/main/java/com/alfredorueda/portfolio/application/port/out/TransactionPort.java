package com.alfredorueda.portfolio.application.port.out;

import com.alfredorueda.portfolio.domain.Transaction;
import com.alfredorueda.portfolio.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionPort {
    Transaction save(Transaction transaction);
    
    List<Transaction> findByPortfolioId(String portfolioId, Optional<String> ticker, 
                                        Optional<TransactionType> type, Optional<LocalDate> fromDate,
                                        Optional<LocalDate> toDate, Optional<BigDecimal> minAmount,
                                        Optional<BigDecimal> maxAmount);
}
