package com.saloni.banking.service;

import com.saloni.banking.entity.*;
import com.saloni.banking.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankingService {
    private final AccountRepository accounts;
    private final CustomerRepository customers;
    private final TransactionRepository transactions;
    private final AppUserRepository users;
    private final AuditLogRepository audit;
    private final PasswordEncoder encoder;

    public BankingService(AccountRepository accounts, CustomerRepository customers,
                          TransactionRepository transactions, AppUserRepository users,
                          AuditLogRepository audit, PasswordEncoder encoder) {
        this.accounts = accounts;
        this.customers = customers;
        this.transactions = transactions;
        this.users = users;
        this.audit = audit;
        this.encoder = encoder;
    }

    public List<Account> accountsFor(String username, boolean staff) {
        return staff ? accounts.findAll() : accounts.findByCustomerUserUsername(username);
    }

    public List<Customer> allCustomers() { return customers.findAll(); }

    public List<Customer> searchCustomers(String query) {
        if (query == null || query.isBlank()) return allCustomers();
        return customers.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContaining(
                query.trim(), query.trim(), query.trim());
    }

    public List<AuditLog> logs() { return audit.findTop50ByOrderByCreatedAtDesc(); }
    public List<AppUser> allUsers() { return users.findAll(); }
    public List<TransactionRecord> recentTransactions() { return transactions.findTop20ByOrderByTransactionTimeDesc(); }
    public long customerCount() { return customers.count(); }
    public long accountCount() { return accounts.count(); }
    public long activeAccountCount() { return accounts.countByActiveTrue(); }

    public Account get(String number) {
        return accounts.findByAccountNumber(number)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));
    }

    public Account getAuthorized(String number, String username, boolean staff) {
        Account account = get(number);
        if (!staff && (account.getCustomer().getUser() == null
                || !account.getCustomer().getUser().getUsername().equals(username))) {
            throw new IllegalArgumentException("You can only access your own account.");
        }
        return account;
    }

    @Transactional
    public void deposit(String number, BigDecimal amount, String by) {
        change(number, amount, "DEPOSIT", by);
    }

    @Transactional
    public void withdraw(String number, BigDecimal amount, String by) {
        change(number, amount, "WITHDRAWAL", by);
    }

    private void change(String number, BigDecimal amount, String type, String by) {
        validateAmount(amount);
        Account account = get(number);
        if (!account.isActive()) throw new IllegalArgumentException("Account is inactive.");
        if ("WITHDRAWAL".equals(type) && account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        BigDecimal newBalance = "DEPOSIT".equals(type)
                ? account.getBalance().add(amount)
                : account.getBalance().subtract(amount);

        account.setBalance(newBalance);
        accounts.save(account);
        transactions.save(new TransactionRecord(account, type, amount, newBalance, by));
        audit.save(new AuditLog(by, type, number + " / ₹" + amount));
    }

    @Transactional
    public void transfer(String fromNumber, String toNumber, BigDecimal amount, String by) {
        validateAmount(amount);
        if (fromNumber.equals(toNumber)) throw new IllegalArgumentException("Source and destination accounts must be different.");

        Account from = get(fromNumber);
        Account to = get(toNumber);

        if (!from.isActive() || !to.isActive()) throw new IllegalArgumentException("Both accounts must be active.");
        if (from.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Insufficient balance.");

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accounts.save(from);
        accounts.save(to);

        transactions.save(new TransactionRecord(from, "TRANSFER_OUT", amount, from.getBalance(), by));
        transactions.save(new TransactionRecord(to, "TRANSFER_IN", amount, to.getBalance(), by));
        audit.save(new AuditLog(by, "TRANSFER", fromNumber + " -> " + toNumber + " / ₹" + amount));
    }

    @Transactional
    public Customer createCustomer(String name, String email, String phone,
                                   String username, String password, String performedBy) {
        validateText(name, "Customer name");
        validateText(username, "Username");
        validateText(password, "Password");
        if (users.existsByUsername(username)) throw new IllegalArgumentException("Username already exists.");

        AppUser user = users.save(new AppUser(username, encoder.encode(password), Role.CUSTOMER));
        Customer customer = new Customer(name.trim(), email == null ? "" : email.trim(), phone == null ? "" : phone.trim());
        customer.setUser(user);
        Customer saved = customers.save(customer);
        audit.save(new AuditLog(performedBy, "CREATE_CUSTOMER", name + " / " + username));
        return saved;
    }

    @Transactional
    public Account createAccount(Long customerId, String number, String type,
                                 BigDecimal opening, String by) {
        if (accounts.findByAccountNumber(number).isPresent()) throw new IllegalArgumentException("Account number already exists.");
        if (opening == null || opening.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Opening balance cannot be negative.");
        Customer customer = customers.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        Account account = new Account();
        account.setAccountNumber(number.trim());
        account.setAccountType(type);
        account.setBalance(opening);
        account.setCustomer(customer);
        Account saved = accounts.save(account);

        if (opening.compareTo(BigDecimal.ZERO) > 0) {
            transactions.save(new TransactionRecord(saved, "DEPOSIT", opening, opening, by));
        }
        audit.save(new AuditLog(by, "CREATE_ACCOUNT", number));
        return saved;
    }

    @Transactional
    public void deleteAccount(Long id, String by) {
        Account account = accounts.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found."));
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Account balance must be zero before deletion.");
        }
        String number = account.getAccountNumber();
        accounts.delete(account);
        audit.save(new AuditLog(by, "DELETE_ACCOUNT", number));
    }

    @Transactional
    public void setAccountActive(Long id, boolean active, String by) {
        Account account = accounts.findById(id).orElseThrow(() -> new IllegalArgumentException("Account not found."));
        account.setActive(active);
        accounts.save(account);
        audit.save(new AuditLog(by, active ? "ACTIVATE_ACCOUNT" : "DEACTIVATE_ACCOUNT", account.getAccountNumber()));
    }

    @Transactional
    public void deleteCustomer(Long id, String by) {
        if (accounts.countByCustomerId(id) > 0) throw new IllegalArgumentException("Delete customer accounts first.");
        Customer customer = customers.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found."));
        String name = customer.getFullName();
        if (customer.getUser() != null) users.delete(customer.getUser());
        customers.delete(customer);
        audit.save(new AuditLog(by, "DELETE_CUSTOMER", name));
    }

    public List<TransactionRecord> history(String number) {
        return transactions.findTop20ByAccountAccountNumberOrderByTransactionTimeDesc(number);
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        AppUser user = users.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found."));
        if (!encoder.matches(currentPassword, user.getPassword())) throw new IllegalArgumentException("Current password is incorrect.");
        if (newPassword == null || newPassword.length() < 6) throw new IllegalArgumentException("New password must contain at least 6 characters.");
        user.setPassword(encoder.encode(newPassword));
        users.save(user);
        audit.save(new AuditLog(username, "CHANGE_PASSWORD", "Password changed"));
    }

    @Transactional
    public void updateUser(Long id, Role role, boolean enabled, String by) {
        AppUser user = users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found."));
        user.setRole(role);
        user.setEnabled(enabled);
        users.save(user);
        audit.save(new AuditLog(by, "UPDATE_USER", user.getUsername() + " -> " + role + ", enabled=" + enabled));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
    }

    private void validateText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required.");
    }
}
