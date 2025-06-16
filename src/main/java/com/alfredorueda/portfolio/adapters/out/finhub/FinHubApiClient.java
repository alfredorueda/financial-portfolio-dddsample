package com.alfredorueda.portfolio.adapters.out.finhub;

import com.alfredorueda.portfolio.application.port.out.StockPricePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class FinHubApiClient implements StockPricePort {
    
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    
    // Simple cache to avoid repeated API calls during demo
    private final Map<String, BigDecimal> priceCache = new HashMap<>();
    
    public FinHubApiClient(
            @Value("${finhub.api.url}") String apiUrl,
            @Value("${finhub.api.key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        
        // Add some initial mock data
        priceCache.put("AAPL", new BigDecimal("175.50"));
        priceCache.put("MSFT", new BigDecimal("380.35"));
        priceCache.put("GOOGL", new BigDecimal("145.22"));
        priceCache.put("AMZN", new BigDecimal("178.75"));
        priceCache.put("TSLA", new BigDecimal("210.45"));
    }
    
    @Override
    public BigDecimal fetchStockPrice(String ticker) {
        // In a real implementation, we would call the actual FinHub API
        // For this demo, we'll just return mock data
        
        // Check cache first
        if (priceCache.containsKey(ticker)) {
            return priceCache.get(ticker);
        }
        
        // For any other ticker, generate a random price between 50 and 500
        BigDecimal randomPrice = BigDecimal.valueOf(
                Math.round((Math.random() * 450 + 50) * 100.0) / 100.0);
        
        // Add to cache
        priceCache.put(ticker, randomPrice);
        
        return randomPrice;
    }
}
