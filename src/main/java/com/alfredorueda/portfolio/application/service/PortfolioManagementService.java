package com.alfredorueda.portfolio.application.service;

import com.alfredorueda.portfolio.application.port.in.PortfolioManagementUseCase;
import com.alfredorueda.portfolio.application.port.out.LoadPortfolioPort;
import com.alfredorueda.portfolio.application.port.out.SavePortfolioPort;
import com.alfredorueda.portfolio.application.port.out.TransactionPort;
import com.alfredorueda.portfolio.domain.Portfolio;
import com.alfredorueda.portfolio.domain.Transaction;
import com.alfredorueda.portfolio.domain.exception.PortfolioNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service responsible for basic portfolio management operations
 */
@Service
public class PortfolioManagementService implements PortfolioManagementUseCase {
    
    private final LoadPortfolioPort loadPortfolioPort;
    private final SavePortfolioPort savePortfolioPort;
    private final TransactionPort transactionPort;
    
    public PortfolioManagementService(
            LoadPortfolioPort loadPortfolioPort,
            SavePortfolioPort savePortfolioPort,
            TransactionPort transactionPort) {
        this.loadPortfolioPort = loadPortfolioPort;
        this.savePortfolioPort = savePortfolioPort;
        this.transactionPort = transactionPort;
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
}