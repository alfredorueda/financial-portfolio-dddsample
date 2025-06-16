package com.alfredorueda.portfolio.application.port.out;

import com.alfredorueda.portfolio.domain.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoadTransactionsPort {
    List<Transaction> loadAllTransactions(
            String portfolioId,
            Optional<String> ticker,
            Optional<String> type,
            Optional<LocalDate> fromDate,
            Optional<LocalDate> toDate,
            Optional<BigDecimal> minAmount,
            Optional<BigDecimal> maxAmount
    );

    Optional<Transaction> findById(String transactionId);
}