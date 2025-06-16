package com.alfredorueda.portfolio.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Transaction represents a financial activity within a portfolio.
 * 
 * In DDD terms, this is a Value Object that records a financial event.
 * Each transaction is immutable and represents one of four activities:
 * - Deposits (adding money to the portfolio)
 * - Withdrawals (removing money from the portfolio)
 * - Purchases (buying stocks)
 * - Sales (selling stocks)
 * 
 * Think of Transactions as your financial history or ledger.
 * They track every money movement and stock trade, allowing you to:
 * - Review your investment history
 * - Calculate profits and losses
 * - Analyze your investment performance over time
 * 
 * Transactions are created through factory methods that ensure all required data
 * for each type of transaction is properly recorded.
 */
@Entity
public class Transaction {
    @Id 
    private String id;
    
    private String portfolioId;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    private String ticker;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private BigDecimal profit;
    private LocalDateTime timestamp;

    protected Transaction() {}
    
    private Transaction(String id, String portfolioId, TransactionType type, String ticker, 
                        int quantity, BigDecimal unitPrice, BigDecimal totalAmount, 
                        BigDecimal profit, LocalDateTime timestamp) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.type = type;
        this.ticker = ticker;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.profit = profit;
        this.timestamp = timestamp;
    }
    
    public static Transaction createDeposit(String portfolioId, BigDecimal amount) {
        return new Transaction(
            UUID.randomUUID().toString(),
            portfolioId,
            TransactionType.DEPOSIT,
            null,
            0,
            BigDecimal.ZERO,
            amount,
            BigDecimal.ZERO,
            LocalDateTime.now()
        );
    }
    
    public static Transaction createWithdrawal(String portfolioId, BigDecimal amount) {
        return new Transaction(
            UUID.randomUUID().toString(),
            portfolioId,
            TransactionType.WITHDRAWAL,
            null,
            0,
            BigDecimal.ZERO,
            amount,
            BigDecimal.ZERO,
            LocalDateTime.now()
        );
    }
    
    public static Transaction createPurchase(String portfolioId, String ticker, 
                                            int quantity, BigDecimal unitPrice) {
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return new Transaction(
            UUID.randomUUID().toString(),
            portfolioId,
            TransactionType.PURCHASE,
            ticker,
            quantity,
            unitPrice,
            totalAmount,
            BigDecimal.ZERO,
            LocalDateTime.now()
        );
    }
    
    public static Transaction createSale(String portfolioId, String ticker, 
                                       int quantity, BigDecimal unitPrice, 
                                       BigDecimal totalAmount, BigDecimal profit) {
        return new Transaction(
            UUID.randomUUID().toString(),
            portfolioId,
            TransactionType.SALE,
            ticker,
            quantity,
            unitPrice,
            totalAmount,
            profit,
            LocalDateTime.now()
        );
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getPortfolioId() {
        return portfolioId;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public String getTicker() {
        return ticker;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public BigDecimal getProfit() {
        return profit;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
