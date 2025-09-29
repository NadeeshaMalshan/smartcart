package com.group35.smartcart.repository;

import com.group35.smartcart.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by username
    List<Order> findByUsername(String username);
    
    // Find orders by username ordered by creation date (newest first)
    List<Order> findByUsernameOrderByCreatedAtDesc(String username);
    
    // Find orders by status
    List<Order> findByOrderStatus(String orderStatus);
    
    // Find orders by username and status
    List<Order> findByUsernameAndOrderStatus(String username, String orderStatus);
    
    // Find latest order by username
    Optional<Order> findFirstByUsernameOrderByCreatedAtDesc(String username);
    
    // Count orders by username
    long countByUsername(String username);
    
    // Count orders by status
    long countByOrderStatus(String orderStatus);
}
