package com.saloni.banking.config;

import com.saloni.banking.entity.*;
import com.saloni.banking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(AppUserRepository users,
                           CustomerRepository customers,
                           AccountRepository accounts,
                           PasswordEncoder encoder) {
        return args -> {
            users.findByUsername("admin").orElseGet(() ->
                    users.save(new AppUser("admin", encoder.encode("Admin@123"), Role.ADMIN)));

            users.findByUsername("teller").orElseGet(() ->
                    users.save(new AppUser("teller", encoder.encode("Teller@123"), Role.TELLER)));

            AppUser customerUser = users.findByUsername("customer").orElseGet(() ->
                    users.save(new AppUser("customer", encoder.encode("Customer@123"), Role.CUSTOMER)));

            Customer customer = customers.findByUserUsername("customer").orElseGet(() -> {
                Customer existing = customers.findAll().stream()
                        .filter(c -> "customer@example.com".equalsIgnoreCase(c.getEmail()))
                        .findFirst()
                        .orElse(null);

                if (existing != null) {
                    existing.setUser(customerUser);
                    return customers.save(existing);
                }

                Customer created = new Customer(
                        "Demo Customer",
                        "customer@example.com",
                        "9999999999"
                );
                created.setUser(customerUser);
                return customers.save(created);
            });

            if (accounts.findByAccountNumber("10000001").isEmpty()) {
                Account account = new Account();
                account.setAccountNumber("10000001");
                account.setAccountType("Savings");
                account.setBalance(new BigDecimal("10000.00"));
                account.setCustomer(customer);
                accounts.save(account);
            }
        };
    }
}
