# FinCore Banking System – Spring Boot

A portfolio-ready banking web application built with **Java, Spring Boot, Spring Security, Thymeleaf, Spring Data JPA/Hibernate and PostgreSQL**.

## Features

### Authentication & Security
- Secure login/logout with Spring Security
- BCrypt password hashing
- Role-based access control: ADMIN, TELLER, CUSTOMER
- Customer-specific account visibility
- Access denied page
- Change password with current-password verification

### Customer Management
- Create customer profile and customer login together
- Customer name, email and phone
- Search by name, email or phone
- Admin/Teller customer management
- Admin-only customer deletion

### Account Management
- Savings and Current accounts
- Unique account numbers
- Opening balance
- Active/inactive status
- Admin activate/deactivate
- Admin delete (only when balance is zero)

### Banking Operations
- Deposit
- Withdrawal
- Insufficient-balance validation
- Account active-status validation
- Fund transfer between accounts
- Atomic transfer using `@Transactional`
- Transaction history
- Recent transaction dashboard

### Administration
- User management
- Change user role and enabled/disabled status
- Audit logs for important operations
- Dashboard statistics

## Demo Accounts

| Role | Username | Password |
|---|---|---|
| ADMIN | admin | Admin@123 |
| TELLER | teller | Teller@123 |
| CUSTOMER | customer | Customer@123 |

Demo account: `10000001` with an opening balance of `₹10,000`.

## Technologies

- Java 17+
- Spring Boot 3.5
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Thymeleaf
- HTML/CSS
- Maven

## Run in Spring Tool Suite

1. Create PostgreSQL database named `banking_management`.
2. Open the project as **Existing Maven Project**.
3. Update `src/main/resources/application.properties` with your PostgreSQL password.
4. Run `BankingManagementApplication.java` as **Spring Boot App**.
5. Open `http://localhost:8081/login`.

Spring Boot/JPA can create or update the schema. `database.sql` is supplied for a fresh/manual PostgreSQL setup.

## Important

If your existing database already contains the older version, keep your current working database. Do not run a destructive `DROP TABLE` script unless you intentionally want to erase its data.
