package com.alfredorueda.portfolio.application.port.out;

import java.math.BigDecimal;

public interface StockPricePort {
    BigDecimal fetchStockPrice(String ticker);
}
