package com.saloni.banking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountType = "Savings";

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Account() {}

    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Customer getCustomer() { return customer; }

    public void setAccountNumber(String value) { accountNumber = value; }
    public void setAccountType(String value) { accountType = value; }
    public void setBalance(BigDecimal value) { balance = value; }
    public void setActive(boolean value) { active = value; }
    public void setCustomer(Customer value) { customer = value; }
}
