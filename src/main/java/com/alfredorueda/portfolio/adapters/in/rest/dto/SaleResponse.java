package com.alfredorueda.portfolio.adapters.in.rest.dto;

import com.alfredorueda.portfolio.domain.SellResult;
import java.math.BigDecimal;

public record SaleResponse(BigDecimal proceeds, BigDecimal costBasis, BigDecimal profit) {
    // Constructor that accepts a SellResult
    public SaleResponse(SellResult result) {
        this(result.proceeds(), result.costBasis(), result.profit());
    }
}