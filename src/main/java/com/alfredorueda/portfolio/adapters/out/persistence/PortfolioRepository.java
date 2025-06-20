package com.alfredorueda.portfolio.adapters.out.persistence;

import com.alfredorueda.portfolio.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, String> {
    // TODO:
    /*
    The N+1 performance problem occurs when lazy fetching is used, and multiple queries are executed to fetch related entities one by one. To avoid this issue, you can use **eager fetching** or **fetch joins** in JPQL/HQL queries, depending on the use case.

### Recommendations:
1. **Lazy Fetching**:
   - Use lazy fetching (`FetchType.LAZY`) by default for collections to avoid loading unnecessary data.
   - Explicitly fetch related entities using **fetch joins** when needed.

2. **Eager Fetching**:
   - Use eager fetching (`FetchType.EAGER`) cautiously for relationships that are always required and have predictable sizes.
   - Avoid eager fetching for large collections or relationships that are rarely accessed, as it can lead to unnecessary data loading.

### Solution: Use Fetch Joins
Fetch joins in JPQL/HQL allow you to fetch related entities in a single query, avoiding the N+1 problem while keeping lazy fetching as the default.

Example:

```java
// Fetch Portfolio with its Holdings and Lots in a single query
@Query("SELECT p FROM Portfolio p JOIN FETCH p.holdings h JOIN FETCH h.lots WHERE p.id = :portfolioId")
Portfolio findPortfolioWithHoldingsAndLots(@Param("portfolioId") String portfolioId);
```

### Key Points:
- Prefer lazy fetching for flexibility and use fetch joins for specific queries to optimize performance.
- Avoid eager fetching for large collections unless absolutely necessary.
     */
}
