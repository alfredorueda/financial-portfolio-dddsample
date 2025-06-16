package com.alfredorueda.portfolio.adapters.in.rest.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
    String message,
    int statusCode,
    LocalDateTime timestamp,
    String path
) {
    public static ErrorResponse of(String message, int statusCode, String path) {
        return new ErrorResponse(message, statusCode, LocalDateTime.now(), path);
    }
}