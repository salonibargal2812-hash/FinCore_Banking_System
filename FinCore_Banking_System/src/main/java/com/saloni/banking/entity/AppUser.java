package com.saloni.banking.entity;

import jakarta.persistence.*;

@Entity
@Table(name="app_users")
public class AppUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true, nullable=false) private String username;
    @Column(nullable=false) private String password;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
    private boolean enabled=true;
    public AppUser() {}
    public AppUser(String username,String password,Role role){this.username=username;this.password=password;this.role=role;}
    public Long getId(){return id;} public String getUsername(){return username;} public String getPassword(){return password;} public Role getRole(){return role;} public boolean isEnabled(){return enabled;}
    public void setUsername(String v){username=v;} public void setPassword(String v){password=v;} public void setRole(Role v){role=v;} public void setEnabled(boolean v){enabled=v;}
}
