package com.alfredorueda.portfolio.application.port.in;

import java.math.BigDecimal;

public interface StockPriceUseCase {
    BigDecimal getCurrentPrice(String ticker);
}
