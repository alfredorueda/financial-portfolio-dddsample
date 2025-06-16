package com.alfredorueda.portfolio.application.port.out;

import com.alfredorueda.portfolio.domain.Portfolio;
import java.util.Optional;

public interface LoadPortfolioPort {
    Optional<Portfolio> findById(String id);
}
