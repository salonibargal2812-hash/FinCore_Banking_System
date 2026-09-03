package com.saloni.banking.repository;

import com.saloni.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String number);
    List<Account> findByCustomerId(Long id);
    List<Account> findByCustomerUserUsername(String username);
    long countByCustomerId(Long id);
    long countByActiveTrue();
}
