package com.alfredorueda.portfolio.adapters.in.rest;

import com.alfredorueda.portfolio.adapters.in.rest.dto.*;
import com.alfredorueda.portfolio.application.port.in.PortfolioAnalysisUseCase;
import com.alfredorueda.portfolio.application.port.in.PortfolioManagementUseCase;
import com.alfredorueda.portfolio.application.port.in.StockTradingUseCase;
import com.alfredorueda.portfolio.application.port.in.dto.TransactionFilter;
import com.alfredorueda.portfolio.domain.InvestmentSummaryDto;
import com.alfredorueda.portfolio.domain.Portfolio;
import com.alfredorueda.portfolio.domain.SellResult;
import com.alfredorueda.portfolio.domain.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {
    
    private final PortfolioManagementUseCase portfolioManagementUseCase;
    private final StockTradingUseCase stockTradingUseCase;
    private final PortfolioAnalysisUseCase portfolioAnalysisUseCase;
    
    public PortfolioController(
            PortfolioManagementUseCase portfolioManagementUseCase,
            StockTradingUseCase stockTradingUseCase,
            PortfolioAnalysisUseCase portfolioAnalysisUseCase) {
        this.portfolioManagementUseCase = portfolioManagementUseCase;
        this.stockTradingUseCase = stockTradingUseCase;
        this.portfolioAnalysisUseCase = portfolioAnalysisUseCase;
    }
    
    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody CreatePortfolioRequest request) {
        Portfolio portfolio = portfolioManagementUseCase.createPortfolio(request.ownerName());
        return new ResponseEntity<>(portfolio, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable String id) {
        Portfolio portfolio = portfolioManagementUseCase.getPortfolio(id);
        return ResponseEntity.ok(portfolio);
    }
    
    @PostMapping("/{id}/deposits")
    public ResponseEntity<Void> deposit(@PathVariable String id, @RequestBody DepositRequest request) {
        portfolioManagementUseCase.deposit(id, request.amount());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/withdrawals")
    public ResponseEntity<Void> withdraw(@PathVariable String id, @RequestBody WithdrawalRequest request) {
        portfolioManagementUseCase.withdraw(id, request.amount());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/purchases")
    public ResponseEntity<Void> buyStock(@PathVariable String id, @RequestBody PurchaseRequest request) {
        stockTradingUseCase.buyStock(id, request.ticker(), request.quantity());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/sales")
    public ResponseEntity<SaleResponse> sellStock(@PathVariable String id, @RequestBody SaleRequest request) {
        SellResult result = stockTradingUseCase.sellStock(id, request.ticker(), request.quantity());
        return ResponseEntity.ok(new SaleResponse(result));
    }
    
    @GetMapping("/{id}/transactions")
    public ResponseEntity<TransactionListResponse> getTransactions(
            @PathVariable String id,
            @RequestParam(required = false) String ticker,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount) {

        TransactionFilter filter = new TransactionFilter(id)
                .withTicker(ticker)
                .withType(type)
                .withDateRange(fromDate, toDate)
                .withAmountRange(minAmount, maxAmount);

        List<Transaction> transactions = portfolioAnalysisUseCase.getTransactions(filter);

        return ResponseEntity.ok(new TransactionListResponse(transactions));
    }
    
    @GetMapping("/{id}/performance")
    public ResponseEntity<PerformanceResponse> getPortfolioPerformance(
            @PathVariable String id,
            @RequestParam(required = false) Integer limit) {
        
        List<InvestmentSummaryDto> performance = portfolioAnalysisUseCase.getPortfolioPerformance(
                id, Optional.ofNullable(limit));
        
        return ResponseEntity.ok(new PerformanceResponse(performance));
    }
}
