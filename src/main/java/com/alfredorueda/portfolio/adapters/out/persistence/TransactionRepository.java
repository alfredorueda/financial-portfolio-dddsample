package com.alfredorueda.portfolio.adapters.out.persistence;

import com.alfredorueda.portfolio.domain.Transaction;
import com.alfredorueda.portfolio.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    
    @Query("SELECT t FROM Transaction t WHERE t.portfolioId = :portfolioId " +
           "AND (:ticker IS NULL OR t.ticker = :ticker) " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:fromDate IS NULL OR t.timestamp >= :fromDateTime) " +
           "AND (:toDate IS NULL OR t.timestamp <= :toDateTime) " +
           "AND (:minAmount IS NULL OR t.totalAmount >= :minAmount) " +
           "AND (:maxAmount IS NULL OR t.totalAmount <= :maxAmount) " +
           "ORDER BY t.timestamp DESC")
    List<Transaction> findByPortfolioIdWithFilters(
            @Param("portfolioId") String portfolioId,
            @Param("ticker") String ticker,
            @Param("type") TransactionType type,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);
}
