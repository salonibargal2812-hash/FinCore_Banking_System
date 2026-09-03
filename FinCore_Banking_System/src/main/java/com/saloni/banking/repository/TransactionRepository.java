package com.saloni.banking.repository;

import com.saloni.banking.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
    List<TransactionRecord> findTop20ByAccountAccountNumberOrderByTransactionTimeDesc(String number);
    List<TransactionRecord> findTop20ByOrderByTransactionTimeDesc();
}
