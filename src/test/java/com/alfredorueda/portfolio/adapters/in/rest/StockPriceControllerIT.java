package com.alfredorueda.portfolio.adapters.in.rest;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockPriceControllerIT {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }
    
    @Test
    public void testGetStockPrice() {
        given()
            .when()
            .get("/api/stocks/{ticker}/price", "AAPL")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("ticker", equalTo("AAPL"))
            .body("price", greaterThan(0.0f));
    }
    
    @Test
    public void testGetRandomStockPrice() {
        given()
            .when()
            .get("/api/stocks/{ticker}/price", "RANDOM")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("ticker", equalTo("RANDOM"))
            .body("price", greaterThan(0.0f));
    }
}
