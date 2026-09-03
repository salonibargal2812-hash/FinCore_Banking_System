package com.saloni.banking.config;

import com.saloni.banking.repository.AppUserRepository;
import org.springframework.context.annotation.*; import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.core.userdetails.*; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.SecurityFilterChain;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService userDetailsService(AppUserRepository repo){return username -> repo.findByUsername(username).filter(u->u.isEnabled()).map(u->User.withUsername(u.getUsername()).password(u.getPassword()).roles(u.getRole().name()).build()).orElseThrow(()->new UsernameNotFoundException("User not found"));}
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
  http.authorizeHttpRequests(a->a.requestMatchers("/css/**","/login").permitAll().anyRequest().authenticated())
   .formLogin(f->f.loginPage("/login").defaultSuccessUrl("/dashboard",true).permitAll())
   .logout(l->l.logoutSuccessUrl("/login?logout").permitAll())
   .exceptionHandling(e->e.accessDeniedPage("/access-denied"));
  return http.build();
 }
}
