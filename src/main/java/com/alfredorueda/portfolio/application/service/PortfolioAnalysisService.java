package com.alfredorueda.portfolio.application.service;

import com.alfredorueda.portfolio.application.port.in.PortfolioAnalysisUseCase;
import com.alfredorueda.portfolio.application.port.in.dto.TransactionFilter;
import com.alfredorueda.portfolio.application.port.out.LoadPortfolioPort;
import com.alfredorueda.portfolio.application.port.out.StockPricePort;
import com.alfredorueda.portfolio.application.port.out.TransactionPort;
import com.alfredorueda.portfolio.domain.*;
import com.alfredorueda.portfolio.domain.exception.PortfolioNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for portfolio analysis and reporting operations
 */
@Service
public class PortfolioAnalysisService implements PortfolioAnalysisUseCase {
    
    private final LoadPortfolioPort loadPortfolioPort;
    private final TransactionPort transactionPort;
    private final StockPricePort stockPricePort;
    
    public PortfolioAnalysisService(
            LoadPortfolioPort loadPortfolioPort,
            TransactionPort transactionPort,
            StockPricePort stockPricePort) {
        this.loadPortfolioPort = loadPortfolioPort;
        this.transactionPort = transactionPort;
        this.stockPricePort = stockPricePort;
    }
    
    private Portfolio getPortfolio(String id) {
        return loadPortfolioPort.findById(id)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found with id: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(TransactionFilter filter) {
        // Verify portfolio exists
        getPortfolio(filter.getPortfolioId());



        return transactionPort.findByPortfolioId(
                filter.getPortfolioId(),
                filter.getTicker(),
                filter.getType().map(TransactionType::valueOf),
                filter.getFromDate(),
                filter.getToDate(),
                filter.getMinAmount(),
                filter.getMaxAmount()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InvestmentSummaryDto> getPortfolioPerformance(String portfolioId, Optional<Integer> limit) {
        // Verify portfolio exists
        getPortfolio(portfolioId);
        
        // Get all transactions for this portfolio
        List<Transaction> allTransactions = transactionPort.findByPortfolioId(
                portfolioId, 
                Optional.empty(), 
                Optional.empty(), 
                Optional.empty(), 
                Optional.empty(), 
                Optional.empty(), 
                Optional.empty());
        
        // Group transactions by ticker
        Map<String, List<Transaction>> transactionsByTicker = allTransactions.stream()
                .filter(t -> t.getTicker() != null)
                .collect(Collectors.groupingBy(Transaction::getTicker));
        
        List<InvestmentSummaryDto> summaries = new ArrayList<>();
        
        for (Map.Entry<String, List<Transaction>> entry : transactionsByTicker.entrySet()) {
            String ticker = entry.getKey();
            List<Transaction> tickerTransactions = entry.getValue();
            
            // Calculate performance metrics for this ticker
            InvestmentSummaryDto summary = calculateTickerPerformance(ticker, tickerTransactions);
            if (summary != null) {
                summaries.add(summary);
            }
        }
        
        // Sort by profit (descending)
        summaries.sort((a, b) -> b.totalProfit().compareTo(a.totalProfit()));
        
        // Apply limit if specified
        if (limit.isPresent() && limit.get() < summaries.size()) {
            return summaries.subList(0, limit.get());
        }
        
        return summaries;
    }
    
    private InvestmentSummaryDto calculateTickerPerformance(String ticker, List<Transaction> transactions) {
        int totalSharesPurchased = 0;
        int totalSharesSold = 0;
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalProceeds = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.PURCHASE) {
                totalSharesPurchased += t.getQuantity();
                totalInvested = totalInvested.add(t.getTotalAmount());
            } else if (t.getType() == TransactionType.SALE) {
                totalSharesSold += t.getQuantity();
                totalProceeds = totalProceeds.add(t.getTotalAmount());
                totalProfit = totalProfit.add(t.getProfit());
            }
        }
        
        // Only include tickers that still have shares
        int remainingShares = totalSharesPurchased - totalSharesSold;
        if (remainingShares > 0) {
            BigDecimal averageBuyPrice = totalInvested.divide(
                    BigDecimal.valueOf(totalSharesPurchased), 
                    2, 
                    RoundingMode.HALF_UP);
            
            BigDecimal currentPrice = stockPricePort.fetchStockPrice(ticker);
            BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(remainingShares));
            BigDecimal unrealizedGain = currentValue.subtract(
                    averageBuyPrice.multiply(BigDecimal.valueOf(remainingShares)));
            
            return new InvestmentSummaryDto(
                    ticker,
                    totalSharesPurchased,
                    totalSharesSold,
                    totalInvested,
                    totalProceeds,
                    totalProfit,
                    averageBuyPrice,
                    currentPrice,
                    unrealizedGain
            );
        }
        
        return null;
    }
}