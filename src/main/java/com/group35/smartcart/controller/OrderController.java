package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Order;
import com.group35.smartcart.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for managing Order operations.
 * Provides RESTful endpoints for order management with proper error handling.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Retrieves all orders.
     * 
     * @return ResponseEntity containing list of all orders
     */
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Orders retrieved successfully");
            response.put("data", orders);
            response.put("count", orders.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("message", "Failed to retrieve orders");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves an order by its ID.
     * 
     * @param id the order ID
     * @return ResponseEntity containing the order if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            Optional<Order> order = orderService.getOrderById(id);
            
            if (order.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Order retrieved successfully");
                response.put("data", order.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("success", "false");
                errorResponse.put("message", "Order not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("message", "Failed to retrieve order");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves orders by username.
     * 
     * @param username the username to search for
     * @return ResponseEntity containing list of orders for the username
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getOrdersByUsername(@PathVariable String username) {
        try {
            List<Order> orders = orderService.getOrdersByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Orders retrieved successfully for user: " + username);
            response.put("data", orders);
            response.put("count", orders.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("message", "Failed to retrieve orders for user: " + username);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves orders by status.
     * 
     * @param status the order status to filter by
     * @return ResponseEntity containing list of orders with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        try {
            List<Order> orders = orderService.getOrdersByStatus(status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Orders retrieved successfully for status: " + status);
            response.put("data", orders);
            response.put("count", orders.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("message", "Failed to retrieve orders for status: " + status);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Creates a new order.
     * 
     * @param order the order to create
     * @return ResponseEntity containing the created order
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            Order savedOrder = orderService.saveOrder(order);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order created successfully");
            response.put("data", savedOrder);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("message", "Failed to create order");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Updates an existing order.
     * 
     * @param id the order ID to update
     * @param order the updated order data
     * @return ResponseEntity containing the updated order
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        try {
            if (!orderService.existsById(id)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("success", "false");
                errorResponse.put("message", "Order not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            order.setPaymentId(id);
            Order updatedOrder = orderService.saveOrder(order);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order updated successfully");
            response.put("data", updatedOrder);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("message", "Failed to update order");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Deletes an order by its ID.
     * 
     * @param id the order ID to delete
     * @return ResponseEntity with deletion confirmation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            if (!orderService.existsById(id)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("success", "false");
                errorResponse.put("message", "Order not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            orderService.deleteOrder(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "Order deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("message", "Failed to delete order");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Gets the total count of orders.
     * 
     * @return ResponseEntity containing the order count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getOrderCount() {
        try {
            long count = orderService.countAllOrders();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order count retrieved successfully");
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("success", "false");
            errorResponse.put("message", "Failed to retrieve order count");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}