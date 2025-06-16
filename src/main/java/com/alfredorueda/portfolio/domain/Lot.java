package com.alfredorueda.portfolio.domain;

import com.alfredorueda.portfolio.domain.exception.InvalidAmountException;
import com.alfredorueda.portfolio.domain.exception.InvalidQuantityException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lot represents a specific purchase of shares for a particular stock.
 * 
 * In DDD terms, this is an Entity that belongs to the Holding aggregate.
 * It tracks the details of a single stock purchase, including:
 * - How many shares were initially purchased
 * - How many shares remain unsold
 * - The price paid per share (unit price)
 * - When the purchase was made
 * 
 * Think of a Lot as a receipt for a specific stock purchase. If you buy Apple
 * shares three times at different prices, you'll have three separate Lots.
 * When selling shares, the system uses these Lots to calculate your profit/loss
 * based on the FIFO (First-In-First-Out) accounting method.
 */
@Entity
public class Lot {
    @Id 
    private String id;
    
    private int remaining;
    private BigDecimal unitPrice;
    private LocalDate purchasedAt;

    protected Lot() {}
    
    public Lot(String id, int quantity, BigDecimal unitPrice) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be positive");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Unit price must be positive");
        }
        
        this.id = id;
        this.remaining = quantity;
        this.unitPrice = unitPrice;
        this.purchasedAt = LocalDate.now();
    }
    
    public void reduce(int qty) {
        if (qty > remaining) {
            throw new InvalidQuantityException("Cannot reduce by more than remaining quantity");
        }
        remaining -= qty;
    }
    
    public boolean isEmpty() {
        return remaining <= 0;
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public int getRemaining() {
        return remaining;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public LocalDate getPurchasedAt() {
        return purchasedAt;
    }
}
