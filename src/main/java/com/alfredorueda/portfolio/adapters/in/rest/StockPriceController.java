package com.alfredorueda.portfolio.adapters.in.rest;

import com.alfredorueda.portfolio.application.port.in.StockPriceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/stocks")
public class StockPriceController {
    
    private final StockPriceUseCase stockPriceUseCase;
    
    public StockPriceController(StockPriceUseCase stockPriceUseCase) {
        this.stockPriceUseCase = stockPriceUseCase;
    }
    
    @GetMapping("/{ticker}/price")
    public ResponseEntity<StockPriceResponse> getStockPrice(@PathVariable String ticker) {
        BigDecimal price = stockPriceUseCase.getCurrentPrice(ticker);
        return ResponseEntity.ok(new StockPriceResponse(ticker, price));
    }
    
    public record StockPriceResponse(String ticker, BigDecimal price) {}
}
