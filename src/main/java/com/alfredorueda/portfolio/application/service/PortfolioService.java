package com.alfredorueda.portfolio.application.service;

import com.alfredorueda.portfolio.application.port.in.PortfolioUseCase;
import com.alfredorueda.portfolio.application.port.out.LoadPortfolioPort;
import com.alfredorueda.portfolio.application.port.out.SavePortfolioPort;
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

@Service
public class PortfolioService implements PortfolioUseCase {
    
    private final LoadPortfolioPort loadPortfolioPort;
    private final SavePortfolioPort savePortfolioPort;
    private final TransactionPort transactionPort;
    private final StockPricePort stockPricePort;
    
    public PortfolioService(LoadPortfolioPort loadPortfolioPort, 
                          SavePortfolioPort savePortfolioPort,
                          TransactionPort transactionPort,
                          StockPricePort stockPricePort) {
        this.loadPortfolioPort = loadPortfolioPort;
        this.savePortfolioPort = savePortfolioPort;
        this.transactionPort = transactionPort;
        this.stockPricePort = stockPricePort;
    }
    
    @Override
    @Transactional
    public Portfolio createPortfolio(String ownerName) {
        Portfolio portfolio = Portfolio.create(ownerName);
        return savePortfolioPort.save(portfolio);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Portfolio getPortfolio(String id) {
        return loadPortfolioPort.findById(id)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found with id: " + id));
    }
    
    @Override
    @Transactional
    public void deposit(String portfolioId, BigDecimal amount) {
        Portfolio portfolio = getPortfolio(portfolioId);
        portfolio.deposit(amount);
        savePortfolioPort.save(portfolio);
        
        Transaction transaction = Transaction.createDeposit(portfolioId, amount);
        transactionPort.save(transaction);
    }
    
    @Override
    @Transactional
    public void withdraw(String portfolioId, BigDecimal amount) {
        Portfolio portfolio = getPortfolio(portfolioId);
        portfolio.withdraw(amount);
        savePortfolioPort.save(portfolio);
        
        Transaction transaction = Transaction.createWithdrawal(portfolioId, amount);
        transactionPort.save(transaction);
    }
    
    @Override
    @Transactional
    public void buyStock(String portfolioId, String ticker, int quantity) {
        // Obtenemos el precio actual del mercado usando StockPricePort
        BigDecimal currentPrice = stockPricePort.fetchStockPrice(ticker);
        
        Portfolio portfolio = getPortfolio(portfolioId);
        portfolio.buy(ticker, quantity, currentPrice);
        savePortfolioPort.save(portfolio);
        
        Transaction transaction = Transaction.createPurchase(portfolioId, ticker, quantity, currentPrice);
        transactionPort.save(transaction);
    }
    
    @Override
    @Transactional
    public SellResult sellStock(String portfolioId, String ticker, int quantity) {
        // Obtenemos el precio actual del mercado usando StockPricePort
        BigDecimal currentPrice = stockPricePort.fetchStockPrice(ticker);
        
        Portfolio portfolio = getPortfolio(portfolioId);
        SellResult result = portfolio.sell(ticker, quantity, currentPrice);
        savePortfolioPort.save(portfolio);
        
        Transaction transaction = Transaction.createSale(
                portfolioId, ticker, quantity, currentPrice, result.proceeds(), result.profit());
        transactionPort.save(transaction);
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(String portfolioId, Optional<String> ticker,
                                           Optional<String> type, Optional<LocalDate> fromDate,
                                           Optional<LocalDate> toDate, Optional<BigDecimal> minAmount,
                                           Optional<BigDecimal> maxAmount) {
        // Verify portfolio exists
        getPortfolio(portfolioId);
        
        Optional<TransactionType> transactionType = type.map(t -> TransactionType.valueOf(t.toUpperCase()));
        
        return transactionPort.findByPortfolioId(portfolioId, ticker, transactionType, 
                                                fromDate, toDate, minAmount, maxAmount);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InvestmentSummaryDto> getPortfolioPerformance(String portfolioId, Optional<Integer> limit) {
        Portfolio portfolio = getPortfolio(portfolioId);
        
        // Get all transactions for this portfolio
        List<Transaction> allTransactions = transactionPort.findByPortfolioId(
                portfolioId, Optional.empty(), Optional.empty(), Optional.empty(), 
                Optional.empty(), Optional.empty(), Optional.empty());
        
        // Group transactions by ticker
        Map<String, List<Transaction>> transactionsByTicker = allTransactions.stream()
                .filter(t -> t.getTicker() != null)
                .collect(Collectors.groupingBy(Transaction::getTicker));
        
        List<InvestmentSummaryDto> summaries = new ArrayList<>();
        
        for (Map.Entry<String, List<Transaction>> entry : transactionsByTicker.entrySet()) {
            String ticker = entry.getKey();
            List<Transaction> tickerTransactions = entry.getValue();
            
            int totalSharesPurchased = 0;
            int totalSharesSold = 0;
            BigDecimal totalInvested = BigDecimal.ZERO;
            BigDecimal totalProceeds = BigDecimal.ZERO;
            BigDecimal totalProfit = BigDecimal.ZERO;
            
            for (Transaction t : tickerTransactions) {
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
                BigDecimal averageBuyPrice = remainingShares > 0 ? 
                        totalInvested.divide(BigDecimal.valueOf(totalSharesPurchased), 2, RoundingMode.HALF_UP) : 
                        BigDecimal.ZERO;
                
                BigDecimal currentPrice = stockPricePort.fetchStockPrice(ticker);
                BigDecimal currentValue = currentPrice.multiply(BigDecimal.valueOf(remainingShares));
                BigDecimal unrealizedGain = currentValue.subtract(
                        averageBuyPrice.multiply(BigDecimal.valueOf(remainingShares)));
                
                summaries.add(new InvestmentSummaryDto(
                        ticker,
                        totalSharesPurchased,
                        totalSharesSold,
                        totalInvested,
                        totalProceeds,
                        totalProfit,
                        averageBuyPrice,
                        currentPrice,
                        unrealizedGain
                ));
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
}
