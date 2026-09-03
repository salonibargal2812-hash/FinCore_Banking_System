package com.saloni.banking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private String email;
    private String phone;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private AppUser user;

    public Customer() {}

    public Customer(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public AppUser getUser() { return user; }

    public void setFullName(String value) { fullName = value; }
    public void setEmail(String value) { email = value; }
    public void setPhone(String value) { phone = value; }
    public void setUser(AppUser value) { user = value; }
}
