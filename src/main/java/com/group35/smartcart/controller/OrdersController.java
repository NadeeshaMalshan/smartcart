package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Customer;
import com.group35.smartcart.entity.Order;
import com.group35.smartcart.entity.Product;
import com.group35.smartcart.repository.OrderRepository;
import com.group35.smartcart.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class OrdersController {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping("/orders")
    public String ordersPage(Model model, HttpSession session) {
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        if (customer == null) {
            // User is not logged in, redirect to login page
            return "redirect:/login?returnUrl=/orders";
        }
        
        // Get all orders for the customer
        List<Order> orders = orderRepository.findByUsernameOrderByCreatedAtDesc(customer.getUsername());
        
        // Convert product IDs to product names and add stock information for each order
        for (Order order : orders) {
            String productInfo = convertProductIdsToNamesWithStock(order.getProductIds());
            order.setProductIds(productInfo); // Reuse the field to store names with stock info
        }
        
        model.addAttribute("title", "SmartCart - My Orders");
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orders);
        model.addAttribute("isLoggedIn", true);
        
        return "orders";
    }
    
    /**
     * Helper method to convert comma-separated product IDs to product names
     */
    private String convertProductIdsToNames(String productIds) {
        if (productIds == null || productIds.trim().isEmpty()) {
            return "No products";
        }
        
        String[] ids = productIds.split(",");
        StringBuilder productNames = new StringBuilder();
        
        for (int i = 0; i < ids.length; i++) {
            try {
                Long productId = Long.parseLong(ids[i].trim());
                Optional<Product> product = productRepository.findById(productId);
                if (product.isPresent()) {
                    productNames.append(product.get().getName());
                } else {
                    productNames.append("Unknown Product (ID: ").append(productId).append(")");
                }
                
                // Add comma separator except for the last item
                if (i < ids.length - 1) {
                    productNames.append(", ");
                }
            } catch (NumberFormatException e) {
                productNames.append("Invalid Product ID: ").append(ids[i].trim());
                if (i < ids.length - 1) {
                    productNames.append(", ");
                }
            }
        }
        
        return productNames.toString();
    }
    
    /**
     * Helper method to convert comma-separated product IDs to product names with stock information
     */
    private String convertProductIdsToNamesWithStock(String productIds) {
        if (productIds == null || productIds.trim().isEmpty()) {
            return "No products";
        }
        
        String[] ids = productIds.split(",");
        StringBuilder productInfo = new StringBuilder();
        
        for (int i = 0; i < ids.length; i++) {
            try {
                Long productId = Long.parseLong(ids[i].trim());
                Optional<Product> product = productRepository.findById(productId);
                if (product.isPresent()) {
                    Product p = product.get();
                    productInfo.append(p.getName());
                    productInfo.append(" (Stock: ").append(p.getStockQuantity() != null ? p.getStockQuantity() : 0).append(")");
                } else {
                    productInfo.append("Unknown Product (ID: ").append(productId).append(")");
                }
                
                // Add comma separator except for the last item
                if (i < ids.length - 1) {
                    productInfo.append(", ");
                }
            } catch (NumberFormatException e) {
                productInfo.append("Invalid Product ID: ").append(ids[i].trim());
                if (i < ids.length - 1) {
                    productInfo.append(", ");
                }
            }
        }
        
        return productInfo.toString();
    }
}