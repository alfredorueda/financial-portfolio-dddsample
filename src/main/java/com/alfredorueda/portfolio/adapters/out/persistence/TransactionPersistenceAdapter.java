package com.alfredorueda.portfolio.adapters.out.persistence;

import com.alfredorueda.portfolio.application.port.out.TransactionPort;
import com.alfredorueda.portfolio.application.port.in.dto.TransactionFilter;
import com.alfredorueda.portfolio.domain.Transaction;
import com.alfredorueda.portfolio.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Component
public class TransactionPersistenceAdapter implements TransactionPort {
    
    private final TransactionRepository transactionRepository;
    
    public TransactionPersistenceAdapter(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
    
    @Override
    public List<Transaction> findByPortfolioId(TransactionFilter filter) {
        LocalDateTime fromDateTime = filter.getFromDate()
                .map(date -> LocalDateTime.of(date, LocalTime.MIN))
                .orElse(null);

        LocalDateTime toDateTime = filter.getToDate()
                .map(date -> LocalDateTime.of(date, LocalTime.MAX))
                .orElse(null);

        return transactionRepository.findByPortfolioIdWithFilters(
                filter.getPortfolioId(),
                filter.getTicker().orElse(null),
                filter.getType().map(TransactionType::valueOf).orElse(null),
                fromDateTime,
                toDateTime,
                filter.getMinAmount().orElse(null),
                filter.getMaxAmount().orElse(null)
        );
    }
}
