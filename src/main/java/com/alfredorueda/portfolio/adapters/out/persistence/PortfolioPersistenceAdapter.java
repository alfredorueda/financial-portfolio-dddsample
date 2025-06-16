package com.alfredorueda.portfolio.adapters.out.persistence;

import com.alfredorueda.portfolio.application.port.out.LoadPortfolioPort;
import com.alfredorueda.portfolio.application.port.out.SavePortfolioPort;
import com.alfredorueda.portfolio.domain.Portfolio;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PortfolioPersistenceAdapter implements LoadPortfolioPort, SavePortfolioPort {
    
    private final PortfolioRepository portfolioRepository;
    
    public PortfolioPersistenceAdapter(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }
    
    @Override
    public Optional<Portfolio> findById(String id) {
        return portfolioRepository.findById(id);
    }
    
    @Override
    public Portfolio save(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }
}
