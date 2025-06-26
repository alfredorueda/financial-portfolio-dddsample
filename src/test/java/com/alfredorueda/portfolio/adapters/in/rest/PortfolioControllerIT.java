package com.alfredorueda.portfolio.adapters.in.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
 * End-to-end integration test for the PortfolioController.
 * This test uses the real implementations of all components without mocking.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest")
public class PortfolioControllerIT {
    
    @LocalServerPort
    private int port;
    
    private String portfolioId;
    
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        
        // Create a test portfolio
        portfolioId = given()
                .contentType(ContentType.JSON)
                .body("{\"ownerName\": \"Test User\"}")
                .when()
                .post("/api/portfolios")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");
    }
    
    @Test
    public void testGetPortfolio() {
        given()
            .when()
            .get("/api/portfolios/{id}", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", equalTo(portfolioId))
            .body("ownerName", equalTo("Test User"))
            .body("balance", comparesEqualTo(0))
            .body("holdings", hasSize(0));
    }
    
    @Test
    public void testGetNonExistentPortfolio() {
        given()
            .when()
            .get("/api/portfolios/non-existent-id")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", containsString("Portfolio not found"))
            .body("code", equalTo("PORTFOLIO_NOT_FOUND"));
    }
    
    @Test
    public void testDeposit() {
        // Deposit funds
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 1000.00}")
            .when()
            .post("/api/portfolios/{id}/deposits", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Verify balance was updated
        given()
            .when()
            .get("/api/portfolios/{id}", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("balance", comparesEqualTo(1000.00f));
    }
    
    @Test
    public void testWithdraw() {
        // First deposit
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 1000.00}")
            .when()
            .post("/api/portfolios/{id}/deposits", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Then withdraw
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 500.00}")
            .when()
            .post("/api/portfolios/{id}/withdrawals", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Verify balance was updated
        given()
            .when()
            .get("/api/portfolios/{id}", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("balance", comparesEqualTo(500.00f));
    }
    
    @Test
    public void testWithdrawInsufficientFunds() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 100.00}")
            .when()
            .post("/api/portfolios/{id}/withdrawals", portfolioId)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("message", containsString("Insufficient funds"))
            .body("code", equalTo("BUSINESS_RULE_VIOLATION"));
    }
    
    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testBuyStock() {
        // Deposit first
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 2000.00}")
            .when()
            .post("/api/portfolios/{id}/deposits", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Get current market price (just for info, not to be passed in the request)
        given()
            .when()
            .get("/api/stocks/{ticker}/price", "AAPL")
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Buy stock - price will be obtained internally through FinHub API
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"AAPL\", \"quantity\": 5}")
            .when()
            .post("/api/portfolios/{id}/purchases", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Verify purchase reflected in portfolio
        given()
            .when()
            .get("/api/portfolios/{id}", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("balance", greaterThan(0.0f))
            .body("holdings", hasSize(1))
            .body("holdings[0].ticker", equalTo("AAPL"))
            .body("holdings[0].lots", hasSize(1))
            .body("holdings[0].lots[0].remaining", equalTo(5));
    }
    
    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    public void testSellStock() {
        // Deposit
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 5000.00}")
            .when()
            .post("/api/portfolios/{id}/deposits", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Get current market price (just for info, not to be passed in the request)
        given()
            .when()
            .get("/api/stocks/{ticker}/price", "MSFT")
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Buy stock - price will be obtained internally through FinHub API
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"MSFT\", \"quantity\": 5}")
            .when()
            .post("/api/portfolios/{id}/purchases", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Add a small delay to avoid rate limiting
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Sell some shares (price will be obtained from FinHub API)
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"MSFT\", \"quantity\": 2}")
            .when()
            .post("/api/portfolios/{id}/sales", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("proceeds", greaterThan(0.0f))
            .body("costBasis", greaterThan(0.0f))
            .body("profit", notNullValue());
        
        // Verify portfolio state
        given()
            .when()
            .get("/api/portfolios/{id}", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("balance", greaterThan(0.0f))
            .body("holdings[0].ticker", equalTo("MSFT"))
            .body("holdings[0].lots[0].remaining", equalTo(3));
    }
    
    @Test
    public void testSellNonExistentStock() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"XYZ\", \"quantity\": 1}")
            .when()
            .post("/api/portfolios/{id}/sales", portfolioId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", containsString("No holding found"))
            .body("code", equalTo("HOLDING_NOT_FOUND"));
    }
    
    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    public void testGetTransactions() {
        // Add some transactions
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 1000.00}")
            .when()
            .post("/api/portfolios/{id}/deposits", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Get current market price (just for info, not to be passed in the request)
        given()
            .when()
            .get("/api/stocks/{ticker}/price", "AAPL")
            .then()
            .statusCode(HttpStatus.OK.value());
            
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"AAPL\", \"quantity\": 2}")
            .when()
            .post("/api/portfolios/{id}/purchases", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Get all transactions
        given()
            .when()
            .get("/api/portfolios/{id}/transactions", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(2))
            .body("[0].type", equalTo("PURCHASE"))
            .body("[1].type", equalTo("DEPOSIT"));
        
        // Get filtered transactions
        given()
            .when()
            .get("/api/portfolios/{id}/transactions?ticker=AAPL", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(1))
            .body("[0].ticker", equalTo("AAPL"));
    }
    
    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    public void testGetPortfolioPerformance() {
        // Add transactions for performance data
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 10000.00}")
            .when()
            .post("/api/portfolios/{id}/deposits", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Get current market prices (just for info, not to be passed in requests)
        given()
            .when()
            .get("/api/stocks/{ticker}/price", "AAPL")
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Add a small delay to avoid rate limiting
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        given()
            .when()
            .get("/api/stocks/{ticker}/price", "MSFT")
            .then()
            .statusCode(HttpStatus.OK.value());
            
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"AAPL\", \"quantity\": 10}")
            .when()
            .post("/api/portfolios/{id}/purchases", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Add a small delay to avoid rate limiting
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
            
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"MSFT\", \"quantity\": 5}")
            .when()
            .post("/api/portfolios/{id}/purchases", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Add a small delay to avoid rate limiting
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Sell some AAPL shares (price will be obtained from FinHub API)
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"AAPL\", \"quantity\": 3}")
            .when()
            .post("/api/portfolios/{id}/sales", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Get performance
        given()
            .when()
            .get("/api/portfolios/{id}/performance", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(2))
            .body("[0].ticker", anyOf(equalTo("AAPL"), equalTo("MSFT")))
            .body("[1].ticker", anyOf(equalTo("AAPL"), equalTo("MSFT")))
            .body("[0].totalSharesPurchased", greaterThan(0))
            .body("[0].currentMarketPrice", greaterThan(0.0f));
        
        // Test with limit
        given()
            .when()
            .get("/api/portfolios/{id}/performance?limit=1", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("$", hasSize(1));
    }
}
