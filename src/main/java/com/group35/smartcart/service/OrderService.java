package com.group35.smartcart.service;

import com.group35.smartcart.entity.Order;
import com.group35.smartcart.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Order operations.
 * Follows SOLID principles and provides business logic for order management.
 */
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Retrieves all orders from the database.
     * 
     * @return List of all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Retrieves an order by its ID.
     * 
     * @param id the order ID
     * @return Optional containing the order if found
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Retrieves orders by username.
     * 
     * @param username the username to search for
     * @return List of orders for the specified username
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByUsername(username);
    }

    /**
     * Retrieves orders by status.
     * 
     * @param status the order status to filter by
     * @return List of orders with the specified status
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByOrderStatus(status);
    }

    /**
     * Saves a new order or updates an existing one.
     * 
     * @param order the order to save
     * @return the saved order
     */
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Deletes an order by its ID.
     * 
     * @param id the order ID to delete
     */
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    /**
     * Checks if an order exists by its ID.
     * 
     * @param id the order ID to check
     * @return true if the order exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return orderRepository.existsById(id);
    }

    /**
     * Counts the total number of orders.
     * 
     * @return the total count of orders
     */
    @Transactional(readOnly = true)
    public long countAllOrders() {
        return orderRepository.count();
    }

    /**
     * Counts orders by username.
     * 
     * @param username the username to count orders for
     * @return the count of orders for the specified username
     */
    @Transactional(readOnly = true)
    public long countOrdersByUsername(String username) {
        return orderRepository.countByUsername(username);
    }

    /**
     * Counts orders by status.
     * 
     * @param status the status to count orders for
     * @return the count of orders with the specified status
     */
    @Transactional(readOnly = true)
    public long countOrdersByStatus(String status) {
        return orderRepository.countByOrderStatus(status);
    }
}