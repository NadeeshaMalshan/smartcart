package com.group35.smartcart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/shopping/**", "/login", "/signup", "/logout", "/cart", "/css/**", "/js/**", "/images/**", 
                                "/employee/login", "/employee/logout", "/employee/dashboard", 
                                "/employee/cashier-dashboard", "/employee/store-manager-dashboard", 
                                "/employee/it-assistant-dashboard", "/employee/delivery-coordinator-dashboard").permitAll()
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable());
        
        return http.build();
    }
}
