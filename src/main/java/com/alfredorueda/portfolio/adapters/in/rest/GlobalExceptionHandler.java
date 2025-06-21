package com.alfredorueda.portfolio.adapters.in.rest;

import com.alfredorueda.portfolio.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

// TODO: unificar los dos gestores de excepciones. Gestionar bien las excepciones de negocio.
//         Devolver los códigos HTTP correctos en cada caso.
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PortfolioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePortfolioNotFoundException(PortfolioNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND");
    }
    
    @ExceptionHandler(HoldingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHoldingNotFoundException(HoldingNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, "HOLDING_NOT_FOUND");
    }
    
    @ExceptionHandler({InsufficientFundsException.class, InvalidAmountException.class, InvalidQuantityException.class})
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(DomainException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, "BUSINESS_RULE_VIOLATION");
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return createErrorResponse("An unexpected error occurred: " + ex.getMessage(), 
                                  HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }
    
    private ResponseEntity<ErrorResponse> createErrorResponse(String message, HttpStatus status, String code) {
        ErrorResponse errorResponse = new ErrorResponse(
                message,
                LocalDateTime.now(),
                code
        );
        return new ResponseEntity<>(errorResponse, status);
    }
    
    public record ErrorResponse(String message, LocalDateTime timestamp, String code) {}
}
