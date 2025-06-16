package com.alfredorueda.portfolio.application.port.out;

import com.alfredorueda.portfolio.domain.Portfolio;

public interface SavePortfolioPort {
    Portfolio save(Portfolio portfolio);
}
