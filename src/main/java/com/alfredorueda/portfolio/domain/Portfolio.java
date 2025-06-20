package com.alfredorueda.portfolio.domain;

import com.alfredorueda.portfolio.domain.exception.HoldingNotFoundException;
import com.alfredorueda.portfolio.domain.exception.InsufficientFundsException;
import com.alfredorueda.portfolio.domain.exception.InvalidAmountException;
import com.alfredorueda.portfolio.domain.exception.InvalidQuantityException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;

/**
 * Portfolio represents an investment portfolio owned by an individual.
 * 
 * This is an Aggregate Root in DDD terms, encapsulating a collection of stocks
 * (Holdings) and providing operations to manage investments.
 * 
 * Think of a portfolio as a personal investment account where you can:
 * - Deposit and withdraw money
 * - Buy and sell stocks
 * - Track your holdings (stocks you own)
 * - Manage your cash balance
 * 
 * The Portfolio enforces business rules like preventing purchases with insufficient funds
 * and ensuring all financial operations use valid amounts.
 */
@Entity
public class Portfolio {
    @Id 
    private String id;
    
    private String ownerName;
    private BigDecimal balance;
    private LocalDate createdAt;

    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "portfolio_id")
    private Set<Holding> holdings = new HashSet<>();

    protected Portfolio() {}
    
    public Portfolio(String id, String ownerName) {
        this.id = id;
        this.ownerName = ownerName;
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDate.now();
    }
    
    public static Portfolio create(String ownerName) {
        return new Portfolio(UUID.randomUUID().toString(), ownerName);
    }
    
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientFundsException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }
    
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }
        this.balance = this.balance.subtract(amount);
    }

    // TODO: quantity and price as value object
    public void buy(String ticker, int quantity, BigDecimal price) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be positive");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Price must be positive");
        }
        
        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));
        if (balance.compareTo(totalCost) < 0) {
            throw new InsufficientFundsException("Insufficient funds to buy " + quantity + " shares of " + ticker);
        }
        
        Holding holding = findOrCreateHolding(ticker);
        holding.buy(quantity, price);
        balance = balance.subtract(totalCost);
    }
    
    public SellResult sell(String ticker, int quantity, BigDecimal price) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be positive");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Price must be positive");
        }
        
        Holding holding = holdings.stream()
                .filter(h -> h.getTicker().equals(ticker))
                .findFirst()
                .orElseThrow(() -> new HoldingNotFoundException("No holding found for ticker: " + ticker));
        
        SellResult result = holding.sell(quantity, price);
        balance = balance.add(result.proceeds());
        
        if (holding.isEmpty()) {
            holdings.remove(holding);
        }
        
        return result;
    }
    
    private Holding findOrCreateHolding(String ticker) {
        return holdings.stream()
                .filter(h -> h.getTicker().equals(ticker))
                .findFirst()
                .orElseGet(() -> {
                    Holding newHolding = Holding.create(ticker);
                    holdings.add(newHolding);
                    return newHolding;
                });
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    
    public Set<Holding> getHoldings() {
        return holdings;
    }
    
    public boolean isEmpty() {
        return holdings.isEmpty();
    }
}
