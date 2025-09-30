package com.group35.smartcart.repository;

import com.group35.smartcart.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    
    // Find customer by username
    Optional<Customer> findByUsername(String username);
    
    // Find customer by email
    Optional<Customer> findByEmail(String email);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find active customers only
    Optional<Customer> findByUsernameAndIsActiveTrue(String username);
    
    // Find customer by username and password (for login)
    Optional<Customer> findByUsernameAndPasswordAndIsActiveTrue(String username, String password);
}
