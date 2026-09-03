package com.saloni.banking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    private LocalDateTime transactionTime = LocalDateTime.now();
    private String performedBy;

    public TransactionRecord() {}

    public TransactionRecord(Account account, String type, BigDecimal amount,
                             BigDecimal balanceAfter, String performedBy) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.performedBy = performedBy;
    }

    public Long getId() { return id; }
    public Account getAccount() { return account; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getTransactionTime() { return transactionTime; }
    public String getPerformedBy() { return performedBy; }
}
