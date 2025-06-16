package com.alfredorueda.portfolio.application.service;

import com.alfredorueda.portfolio.application.port.in.StockPriceUseCase;
import com.alfredorueda.portfolio.application.port.out.StockPricePort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StockPriceService implements StockPriceUseCase {
    
    private final StockPricePort stockPricePort;
    
    public StockPriceService(StockPricePort stockPricePort) {
        this.stockPricePort = stockPricePort;
    }
    
    @Override
    public BigDecimal getCurrentPrice(String ticker) {
        return stockPricePort.fetchStockPrice(ticker);
    }
}
