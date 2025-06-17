package com.alfredorueda.portfolio.domain;

import com.alfredorueda.portfolio.domain.exception.InvalidAmountException;
import com.alfredorueda.portfolio.domain.exception.InvalidQuantityException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;

/**
 * Holding represents the ownership of a specific stock within a portfolio.
 * 
 * In DDD terms, this is an Entity that belongs to the Portfolio aggregate.
 * It tracks all lots (purchases) of a particular stock and handles the selling
 * process using the FIFO (First-In-First-Out) accounting method.
 * 
 * Think of a Holding as your collection of shares for a single company, like Apple or Microsoft.
 * Each time you buy shares of this company, a new "Lot" is created to track that specific purchase.
 * When you sell shares, the oldest ones are sold first (FIFO method).
 * 
 * The Holding enforces business rules such as preventing the sale of more shares than you own.
 */
@Entity
public class Holding {
    @Id 
    private String id;
    
    private String ticker;

    /**
     * Collection implementation note:
     * 
     * Using List<Lot> with @OrderBy is an elegant solution that balances:
     * 
     * 1. DDD principles:
     *    - Accurately models the domain concept of chronologically ordered lots
     *    - Preserves the natural temporal sequence needed for FIFO accounting
     *    - Maintains a clean and intuitive domain model that directly expresses business rules
     * 
     * 2. JPA/Hibernate efficiency:
     *    - Leverages database-level ordering for optimal performance
     *    - Provides excellent performance for typical collection sizes (up to hundreds of elements)
     *    - Avoids complex mapping configurations while maintaining good persistence characteristics
     * 
     * This implementation represents a well-balanced approach that prioritizes both 
     * domain model elegance and persistence efficiency.
     */
    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "holding_id")
    @OrderBy("purchasedAt ASC")
    private List<Lot> lots = new ArrayList<>();

    protected Holding() {}
    
    public Holding(String id, String ticker) {
        this.id = id;
        this.ticker = ticker;
    }
    
    public static Holding create(String ticker) {
        return new Holding(UUID.randomUUID().toString(), ticker);
    }
    
    public void buy(int quantity, BigDecimal unitPrice) {
        Lot lot = new Lot(UUID.randomUUID().toString(), quantity, unitPrice);
        lots.add(lot);
    }
    
    public SellResult sell(int quantity, BigDecimal sellPrice) {
        if (getTotalShares() < quantity) {
            throw new InvalidQuantityException("Not enough shares to sell. Available: " + getTotalShares() + ", Requested: " + quantity);
        }

        int remainingToSell = quantity;
        BigDecimal costBasis = BigDecimal.ZERO;
        
        for (Lot lot : lots) {
            if (remainingToSell <= 0) break;
            
            int sharesSoldFromLot = Math.min(lot.getRemaining(), remainingToSell);
            BigDecimal lotCostBasis = lot.getUnitPrice().multiply(BigDecimal.valueOf(sharesSoldFromLot));
            
            costBasis = costBasis.add(lotCostBasis);
            lot.reduce(sharesSoldFromLot);
            remainingToSell -= sharesSoldFromLot;
        }
        
        // Remove empty lots
        lots.removeIf(Lot::isEmpty);
        
        BigDecimal proceeds = sellPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal profit = proceeds.subtract(costBasis);
        
        return new SellResult(proceeds, costBasis, profit);
    }
    
    public int getTotalShares() {
        return lots.stream()
                .mapToInt(Lot::getRemaining)
                .sum();
    }
    
    public boolean isEmpty() {
        return lots.isEmpty() || getTotalShares() == 0;
    }
    
    public String getId() {
        return id;
    }
    
    public String getTicker() {
        return ticker;
    }
    
    public List<Lot> getLots() {
        return lots;
    }

    // TODO: @Override equals and hashCode methods for proper entity comparison
}
