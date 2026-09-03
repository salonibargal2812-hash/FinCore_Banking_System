package com.saloni.banking.controller;

import com.saloni.banking.service.BankingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final BankingService service;
    public DashboardController(BankingService service) { this.service = service; }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication auth, Model model) {
        boolean staff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TELLER"));
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        model.addAttribute("accounts", service.accountsFor(auth.getName(), staff));
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", role);
        model.addAttribute("customerCount", service.customerCount());
        model.addAttribute("accountCount", service.accountCount());
        model.addAttribute("activeAccountCount", service.activeAccountCount());
        model.addAttribute("recentTransactions", service.recentTransactions());
        return "dashboard";
    }
}
