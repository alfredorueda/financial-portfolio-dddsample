package com.alfredorueda.portfolio.adapters.out.finhub;

import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FinHubClient {
    
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
    
    public FinHubClient(
            RestTemplate restTemplate,
            @Value("${finhub.api.key}") String apiKey,
            @Value("${finhub.api.base-url:https://finnhub.io/api/v1}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }
    
    public BigDecimal getStockPrice(String symbol) {
        String url = String.format("%s/quote?symbol=%s&token=%s", baseUrl, symbol, apiKey);
        FinHubQuoteResponse response = restTemplate.getForObject(url, FinHubQuoteResponse.class);
        
        if (response == null || response.getCurrentPrice() == null) {
            throw new RuntimeException("Failed to retrieve stock price for symbol: " + symbol);
        }
        
        return response.getCurrentPrice();
    }
    
    // Inner class to deserialize the Finnhub API response
    public static class FinHubQuoteResponse {
        private BigDecimal c; // Current price
        
        public BigDecimal getCurrentPrice() {
            return c;
        }
        
        public void setC(BigDecimal c) {
            this.c = c;
        }
    }
}