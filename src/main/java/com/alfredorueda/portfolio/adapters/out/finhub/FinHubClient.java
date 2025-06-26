package com.alfredorueda.portfolio.adapters.out.finhub;

import com.alfredorueda.portfolio.application.port.out.StockPricePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Client for the FinHub API that fetches stock price data
 */
@Primary
@Component
public class FinHubClient implements StockPricePort {
    
    private static final Logger log = LoggerFactory.getLogger(FinHubClient.class);
    private static final String API_URL = "https://finnhub.io/api/v1/quote?symbol=%s&token=%s";
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 500;
    
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final boolean testMode;
    private final Map<String, BigDecimal> mockPrices = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    public FinHubClient(
            RestTemplate restTemplate,
            @Value("${finhub.api.key}") String apiKey,
            @Value("${finhub.api.test-mode:false}") boolean testMode) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.testMode = testMode;
        
        // Initialize mock prices for common tickers
        mockPrices.put("AAPL", new BigDecimal("150.00"));
        mockPrices.put("MSFT", new BigDecimal("300.00"));
        mockPrices.put("GOOGL", new BigDecimal("2800.00"));
        mockPrices.put("AMZN", new BigDecimal("3300.00"));
        mockPrices.put("META", new BigDecimal("330.00"));
        mockPrices.put("TSLA", new BigDecimal("900.00"));
        mockPrices.put("NFLX", new BigDecimal("550.00"));
        mockPrices.put("NVDA", new BigDecimal("220.00"));
        
        log.info("FinHub client initialized with test mode {}", testMode ? "enabled" : "disabled");
    }
    
    @Override
    public BigDecimal fetchStockPrice(String symbol) {
        if (testMode) {
            return getMockPrice(symbol);
        }
        
        try {
            return fetchStockPriceWithRetry(symbol);
        } catch (Exception e) {
            log.warn("Failed to retrieve stock price for {}, falling back to mock price. Error: {}", 
                    symbol, e.getMessage());
            return getMockPrice(symbol);
        }
    }
    
    private BigDecimal fetchStockPriceWithRetry(String symbol) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < MAX_RETRY_ATTEMPTS) {
            try {
                String url = String.format(API_URL, symbol, apiKey);
                log.debug("Fetching stock price for {} (attempt {})", symbol, attempts + 1);
                
                FinHubQuoteResponse response = restTemplate.getForObject(url, FinHubQuoteResponse.class);
                
                if (response == null || response.getCurrentPrice() == null) {
                    throw new RuntimeException("Received null response from FinHub API");
                }
                
                BigDecimal price = response.getCurrentPrice();
                log.info("Successfully fetched price for {}: {}", symbol, price);
                return price;
                
            } catch (Exception e) {
                lastException = e;
                attempts++;
                
                if (attempts < MAX_RETRY_ATTEMPTS) {
                    long backoffMillis = calculateBackoffMillis(attempts);
                    log.warn("Error fetching price for {} (attempt {}). Retrying in {} ms. Error: {}", 
                            symbol, attempts, backoffMillis, e.getMessage());
                    try {
                        TimeUnit.MILLISECONDS.sleep(backoffMillis);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread interrupted during backoff", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("Failed to retrieve stock price for symbol: " + symbol + 
                " after " + MAX_RETRY_ATTEMPTS + " attempts", lastException);
    }
    
    private long calculateBackoffMillis(int attempt) {
        // Exponential backoff with jitter
        long backoff = INITIAL_BACKOFF_MS * (long) Math.pow(2, attempt - 1);
        return backoff + ThreadLocalRandom.current().nextLong(INITIAL_BACKOFF_MS);
    }
    
    private BigDecimal getMockPrice(String symbol) {
        return mockPrices.computeIfAbsent(symbol, this::generateMockPrice);
    }
    
    private BigDecimal generateMockPrice(String symbol) {
        // Use the symbol hashcode to generate a somewhat stable but random-looking price
        int hash = Math.abs(symbol.hashCode());
        BigDecimal basePrice = new BigDecimal(50 + (hash % 450));
        
        // Add some small randomness for variability
        BigDecimal randomFactor = new BigDecimal(0.9 + (random.nextDouble() * 0.2))
                .setScale(2, RoundingMode.HALF_UP);
        
        return basePrice.multiply(randomFactor).setScale(2, RoundingMode.HALF_UP);
    }
}