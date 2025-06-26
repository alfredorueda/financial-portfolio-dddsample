package com.alfredorueda.portfolio.adapters.in.rest;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * End-to-end integration test for the StockPriceController.
 * This test uses the real FinHub API without mocking.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
public class StockPriceControllerIT {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }
    
    /**
     * Tests retrieving a stock price for Apple (AAPL).
     * Uses the real FinHub API with retry logic to handle potential rate limits.
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testGetStockPrice() {
        // Retry logic for handling potential API rate limits
        int maxRetries = 3;
        int retryDelayMs = 1000;
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                given()
                    .when()
                    .get("/api/stocks/{ticker}/price", "AAPL")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("ticker", equalTo("AAPL"))
                    .body("price", greaterThan(0.0f));
                return; // Success, exit retry loop
            } catch (AssertionError e) {
                if (attempt < maxRetries - 1) {
                    // Wait before retrying
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e; // Last attempt failed, propagate the error
                }
            }
        }
    }
    
    /**
     * Tests retrieving a stock price for a valid ticker.
     * Uses the real FinHub API with retry logic to handle potential rate limits.
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testGetValidStockPrice() {
        // Retry logic for handling potential API rate limits
        int maxRetries = 3;
        int retryDelayMs = 1000;
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                given()
                    .when()
                    .get("/api/stocks/{ticker}/price", "MSFT")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("ticker", equalTo("MSFT"))
                    .body("price", greaterThan(0.0f));
                return; // Success, exit retry loop
            } catch (AssertionError e) {
                if (attempt < maxRetries - 1) {
                    // Wait before retrying
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e; // Last attempt failed, propagate the error
                }
            }
        }
    }
}
