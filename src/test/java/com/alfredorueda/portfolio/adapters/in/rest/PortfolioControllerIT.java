package com.alfredorueda.portfolio.adapters.in.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    public void testBuyStock() {
        // Deposit first
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 2000.00}")
            .when()
            .post("/api/portfolios/{id}/deposits", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Buy stock
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"AAPL\", \"quantity\": 5, \"price\": 180.00}")
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
            .body("balance", comparesEqualTo(1100.00f))
            .body("holdings", hasSize(1))
            .body("holdings[0].ticker", equalTo("AAPL"))
            .body("holdings[0].lots", hasSize(1))
            .body("holdings[0].lots[0].remaining", equalTo(5));
    }
    
    @Test
    public void testSellStock() {
        // Deposit
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 2000.00}")
            .when()
            .post("/api/portfolios/{id}/deposits", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Buy stock
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"MSFT\", \"quantity\": 5, \"price\": 350.00}")
            .when()
            .post("/api/portfolios/{id}/purchases", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Sell some shares at a profit
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"MSFT\", \"quantity\": 2, \"price\": 360.00}")
            .when()
            .post("/api/portfolios/{id}/sales", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("proceeds", comparesEqualTo(720.00f))
            .body("profit", greaterThan(0.0f));
        
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
            .body("{\"ticker\": \"XYZ\", \"quantity\": 1, \"price\": 100.00}")
            .when()
            .post("/api/portfolios/{id}/sales", portfolioId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("message", containsString("No holding found"))
            .body("code", equalTo("HOLDING_NOT_FOUND"));
    }
    
    @Test
    public void testGetTransactions() {
        // Add some transactions
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 1000.00}")
            .when()
            .post("/api/portfolios/{id}/deposits", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
            
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"AAPL\", \"quantity\": 2, \"price\": 170.00}")
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
    public void testGetPortfolioPerformance() {
        // Add transactions for performance data
        given()
            .contentType(ContentType.JSON)
            .body("{\"amount\": 10000.00}")
            .when()
            .post("/api/portfolios/{id}/deposits", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
            
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"AAPL\", \"quantity\": 10, \"price\": 170.00}")
            .when()
            .post("/api/portfolios/{id}/purchases", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
            
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"MSFT\", \"quantity\": 5, \"price\": 350.00}")
            .when()
            .post("/api/portfolios/{id}/purchases", portfolioId)
            .then()
            .statusCode(HttpStatus.OK.value());
        
        // Sell some AAPL at a profit
        given()
            .contentType(ContentType.JSON)
            .body("{\"ticker\": \"AAPL\", \"quantity\": 3, \"price\": 175.00}")
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
