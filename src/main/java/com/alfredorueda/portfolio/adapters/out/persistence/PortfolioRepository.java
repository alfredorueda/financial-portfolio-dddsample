package com.alfredorueda.portfolio.adapters.out.persistence;

import com.alfredorueda.portfolio.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, String> {
}
