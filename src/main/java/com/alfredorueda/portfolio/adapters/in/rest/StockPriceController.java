package com.alfredorueda.portfolio.adapters.in.rest;

import com.alfredorueda.portfolio.application.port.in.StockPriceUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for stock price operations
 */
@RestController
@RequestMapping("/api/stocks")
public class StockPriceController {
    
    private static final Logger log = LoggerFactory.getLogger(StockPriceController.class);
    private final StockPriceUseCase stockPriceUseCase;
    
    public StockPriceController(StockPriceUseCase stockPriceUseCase) {
        this.stockPriceUseCase = stockPriceUseCase;
    }
    
    /**
     * Retrieves the current price for a given stock ticker
     * 
     * @param ticker The stock ticker symbol
     * @return ResponseEntity containing the stock ticker and its current price
     */
    @GetMapping("/{ticker}/price")
    public ResponseEntity<Map<String, Object>> getStockPrice(@PathVariable String ticker) {
        log.debug("Fetching price for ticker: {}", ticker);
        
        try {
            BigDecimal price = stockPriceUseCase.getCurrentPrice(ticker);
            
            Map<String, Object> response = new HashMap<>();
            response.put("ticker", ticker);
            response.put("price", price);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching price for ticker {}: {}", ticker, e.getMessage());
            throw e;
        }
    }
}
