package com.alfredorueda.portfolio.application.port.out;

import com.alfredorueda.portfolio.domain.Transaction;

public interface SaveTransactionPort {
    void save(Transaction transaction);
}