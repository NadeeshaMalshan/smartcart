package com.group35.smartcart.repository;

import com.group35.smartcart.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    
    /**
     * Find bill by payment ID
     * @param paymentId the payment ID
     * @return Optional containing the bill if found
     */
    Optional<Bill> findByPaymentId(Long paymentId);
    
    /**
     * Find bill by order ID
     * @param orderId the order ID
     * @return Optional containing the bill if found
     */
    Optional<Bill> findByOrderId(Long orderId);
    
    /**
     * Check if bill exists for a payment
     * @param paymentId the payment ID
     * @return true if bill exists, false otherwise
     */
    boolean existsByPaymentId(Long paymentId);
}
