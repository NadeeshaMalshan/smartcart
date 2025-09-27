package com.group35.smartcart.repository;

import com.group35.smartcart.entity.CustomerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {
    
    // Find payment details by username
    List<CustomerPayment> findByUsername(String username);
    
    // Find active payment details by username
    List<CustomerPayment> findByUsernameAndIsActiveTrue(String username);
    
    // Find latest payment details by username
    Optional<CustomerPayment> findFirstByUsernameAndIsActiveTrueOrderByCreatedAtDesc(String username);
    
    // Check if payment details exist for username
    boolean existsByUsernameAndIsActiveTrue(String username);
}
