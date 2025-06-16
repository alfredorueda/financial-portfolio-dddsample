package com.alfredorueda.portfolio.domain;

import java.math.BigDecimal;

/**
 * InvestmentSummaryDto provides a comprehensive performance overview of a stock investment.
 * 
 * While technically named with the DTO suffix, in this domain model it represents 
 * a read-only Value Object that summarizes investment metrics for a specific stock.
 * 
 * It contains important metrics for investment analysis:
 * - ticker: The stock symbol (e.g., AAPL for Apple)
 * - totalSharesPurchased: Total number of shares ever purchased
 * - totalSharesSold: Total number of shares that have been sold
 * - totalInvested: Total money spent purchasing this stock
 * - totalProceeds: Total money received from selling shares
 * - totalProfit: Realized profit/loss from completed sales (totalProceeds - cost of sold shares)
 * - averageBuyPrice: Average price paid per share across all purchases
 * - currentMarketPrice: Latest price of the stock in the market
 * - unrealizedGain: Potential profit/loss on remaining shares at current market price
 * 
 * This summary enables investors to evaluate their investment performance
 * both for completed trades (realized gains) and current holdings (unrealized gains).
 */
public record InvestmentSummaryDto(
    String ticker,
    int totalSharesPurchased,
    int totalSharesSold,
    BigDecimal totalInvested,
    BigDecimal totalProceeds,
    BigDecimal totalProfit,
    BigDecimal averageBuyPrice,
    BigDecimal currentMarketPrice,
    BigDecimal unrealizedGain
) {
}
