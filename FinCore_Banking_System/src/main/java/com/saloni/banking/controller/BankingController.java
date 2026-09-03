package com.saloni.banking.controller;

import com.saloni.banking.entity.Role;
import com.saloni.banking.service.BankingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/banking")
public class BankingController {
    private final BankingService service;

    public BankingController(BankingService service) { this.service = service; }

    @PostMapping("/deposit")
    public String deposit(@RequestParam String accountNumber, @RequestParam BigDecimal amount, Authentication auth) {
        service.getAuthorized(accountNumber, auth.getName(), isStaff(auth));
        service.deposit(accountNumber, amount, auth.getName());
        return redirect("Deposit successful.");
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam String accountNumber, @RequestParam BigDecimal amount, Authentication auth) {
        service.getAuthorized(accountNumber, auth.getName(), isStaff(auth));
        service.withdraw(accountNumber, amount, auth.getName());
        return redirect("Withdrawal successful.");
    }

    @GetMapping("/transfer")
    public String transferForm(Model model, Authentication auth) {
        model.addAttribute("myAccounts", service.accountsFor(auth.getName(), isStaff(auth)));
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam String fromAccount, @RequestParam String toAccount,
                           @RequestParam BigDecimal amount, Authentication auth) {
        service.getAuthorized(fromAccount, auth.getName(), isStaff(auth));
        service.transfer(fromAccount, toAccount, amount, auth.getName());
        return redirect("Transfer successful.");
    }

    @GetMapping("/history/{number}")
    public String history(@PathVariable String number, Model model, Authentication auth) {
        service.getAuthorized(number, auth.getName(), isStaff(auth));
        model.addAttribute("account", service.get(number));
        model.addAttribute("transactions", service.history(number));
        return "history";
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('ADMIN','TELLER')")
    public String customers(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("customers", service.searchCustomers(q));
        model.addAttribute("query", q == null ? "" : q);
        return "customers";
    }

    @GetMapping("/customers/new")
    @PreAuthorize("hasAnyRole('ADMIN','TELLER')")
    public String customerForm() { return "customer-form"; }

    @PostMapping("/customers")
    @PreAuthorize("hasAnyRole('ADMIN','TELLER')")
    public String createCustomer(@RequestParam String fullName, @RequestParam String email,
                                 @RequestParam String phone, @RequestParam String username,
                                 @RequestParam String password, Authentication auth) {
        service.createCustomer(fullName, email, phone, username, password, auth.getName());
        return "redirect:/banking/customers?success=Customer%20created";
    }

    @PostMapping("/customers/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCustomer(@PathVariable Long id, Authentication auth) {
        service.deleteCustomer(id, auth.getName());
        return "redirect:/banking/customers?success=Customer%20deleted";
    }

    @GetMapping("/accounts/new")
    @PreAuthorize("hasAnyRole('ADMIN','TELLER')")
    public String accountForm(Model model) {
        model.addAttribute("customers", service.allCustomers());
        return "account-form";
    }

    @PostMapping("/accounts")
    @PreAuthorize("hasAnyRole('ADMIN','TELLER')")
    public String createAccount(@RequestParam Long customerId, @RequestParam String accountNumber,
                                @RequestParam String accountType, @RequestParam BigDecimal openingBalance,
                                Authentication auth) {
        service.createAccount(customerId, accountNumber, accountType, openingBalance, auth.getName());
        return "redirect:/dashboard?success=Account%20created";
    }

    @PostMapping("/accounts/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAccount(@PathVariable Long id, Authentication auth) {
        service.deleteAccount(id, auth.getName());
        return "redirect:/dashboard?success=Account%20deleted";
    }

    @PostMapping("/accounts/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleAccount(@PathVariable Long id, @RequestParam boolean active, Authentication auth) {
        service.setAccountActive(id, active, auth.getName());
        return "redirect:/dashboard?success=Account%20status%20updated";
    }

    @GetMapping("/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public String audit(Model model) { model.addAttribute("logs", service.logs()); return "audit"; }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String users(Model model) { model.addAttribute("users", service.allUsers()); return "users"; }

    @PostMapping("/users/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable Long id, @RequestParam Role role,
                             @RequestParam(defaultValue = "false") boolean enabled,
                             Authentication auth) {
        service.updateUser(id, role, enabled, auth.getName());
        return "redirect:/banking/users?success=User%20updated";
    }

    @GetMapping("/password")
    public String passwordForm() { return "password"; }

    @PostMapping("/password")
    public String changePassword(@RequestParam String currentPassword, @RequestParam String newPassword,
                                 Authentication auth) {
        service.changePassword(auth.getName(), currentPassword, newPassword);
        return "redirect:/dashboard?success=Password%20changed";
    }

    private String redirect(String message) { return "redirect:/dashboard?success=" + message.replace(" ", "%20"); }

    private boolean isStaff(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TELLER"));
    }
}
