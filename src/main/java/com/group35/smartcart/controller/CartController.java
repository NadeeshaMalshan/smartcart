package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Customer;
import com.group35.smartcart.entity.CustomerPayment;
import com.group35.smartcart.entity.Order;
import com.group35.smartcart.entity.Product;
import com.group35.smartcart.repository.CustomerPaymentRepository;
import com.group35.smartcart.repository.OrderRepository;
import com.group35.smartcart.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class CartController {
    
    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping("/cart")
    public String cartPage(Model model, HttpSession session) {
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        model.addAttribute("title", "SmartCart - Shopping Cart");
        model.addAttribute("customer", customer);
        model.addAttribute("isLoggedIn", customer != null);
        return "cart";
    }
    
    @GetMapping("/billing")
    public String billingPage(Model model, HttpSession session) {
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        if (customer == null) {
            // User is not logged in, redirect to login page
            return "redirect:/login?returnUrl=/billing";
        }
        
        model.addAttribute("title", "SmartCart - Billing Information");
        model.addAttribute("customer", customer);
        return "billing";
    }
    
    @GetMapping("/payment")
    public String paymentPage(Model model, HttpSession session) {
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        if (customer == null) {
            // User is not logged in, redirect to login page
            return "redirect:/login?returnUrl=/payment";
        }
        
        model.addAttribute("title", "SmartCart - Payment Information");
        model.addAttribute("customer", customer);
        return "payment";
    }
    
    @GetMapping("/review")
    public String reviewPage(Model model, HttpSession session) {
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        if (customer == null) {
            // User is not logged in, redirect to login page
            return "redirect:/login?returnUrl=/review";
        }
        
        model.addAttribute("title", "SmartCart - Review Order");
        model.addAttribute("customer", customer);
        return "review";
    }
    
    @PostMapping("/upload-payment-slip")
    @ResponseBody
    public String uploadPaymentSlip(@RequestParam("paymentSlip") MultipartFile file, HttpSession session) {
        try {
            // Check if user is logged in
            Customer customer = (Customer) session.getAttribute("customer");
            if (customer == null) {
                return "{\"success\": false, \"message\": \"User not logged in\"}";
            }
            
            // Validate file
            if (file.isEmpty()) {
                return "{\"success\": false, \"message\": \"No file uploaded\"}";
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/png") && 
                !contentType.equals("image/jpeg") && !contentType.equals("application/pdf"))) {
                return "{\"success\": false, \"message\": \"Invalid file type. Only PNG, JPG, and PDF files are allowed\"}";
            }
            
            // Validate file size (10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return "{\"success\": false, \"message\": \"File size must be less than 10MB\"}";
            }
            
            // Create payslips directory if it doesn't exist
            String payslipsDir = "src/main/resources/payslips";
            File directory = new File(payslipsDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + "_" + customer.getUsername() + extension;
            
            // Save file
            Path filePath = Paths.get(payslipsDir, uniqueFilename);
            Files.write(filePath, file.getBytes());
            
            return "{\"success\": true, \"message\": \"Payment slip uploaded successfully\", \"filename\": \"" + uniqueFilename + "\"}";
            
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Failed to upload file\"}";
        }
    }
    
    @PostMapping("/save-payment-details")
    @ResponseBody
    public String savePaymentDetails(@RequestParam("bankName") String bankName,
                                   @RequestParam("accountHolderName") String accountHolderName,
                                   @RequestParam("accountNumber") String accountNumber,
                                   HttpSession session) {
        try {
            // Check if user is logged in
            Customer customer = (Customer) session.getAttribute("customer");
            if (customer == null) {
                return "{\"success\": false, \"message\": \"User not logged in\"}";
            }
            
            // Validate input parameters
            if (bankName == null || bankName.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Bank name is required\"}";
            }
            if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Account holder name is required\"}";
            }
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Account number is required\"}";
            }
            
            // Create and save payment details
            CustomerPayment paymentDetails = new CustomerPayment(
                customer.getUsername(),
                bankName.trim(),
                accountHolderName.trim(),
                accountNumber.trim()
            );
            
            customerPaymentRepository.save(paymentDetails);
            
            return "{\"success\": true, \"message\": \"Payment details saved successfully\"}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Failed to save payment details\"}";
        }
    }
    
    @PostMapping("/place-order")
    @ResponseBody
    public String placeOrder(@RequestParam("productIds") String productIds,
                           @RequestParam("productQuantities") String productQuantities,
                           @RequestParam("subtotal") String subtotal,
                           @RequestParam("payslipLocationPath") String payslipLocationPath,
                           @RequestParam("bankName") String bankName,
                           @RequestParam("accountHolderName") String accountHolderName,
                           @RequestParam("accountNumber") String accountNumber,
                           HttpSession session) {
        try {
            // Check if user is logged in
            Customer customer = (Customer) session.getAttribute("customer");
            if (customer == null) {
                return "{\"success\": false, \"message\": \"User not logged in\"}";
            }
            
            // Validate input parameters
            if (productIds == null || productIds.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Product IDs are required\"}";
            }
            if (productQuantities == null || productQuantities.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Product quantities are required\"}";
            }
            if (subtotal == null || subtotal.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Subtotal is required\"}";
            }
            
            // Validate payment details
            if (bankName == null || bankName.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Bank name is required\"}";
            }
            if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Account holder name is required\"}";
            }
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Account number is required\"}";
            }
            
            // Parse subtotal
            BigDecimal subtotalAmount;
            try {
                subtotalAmount = new BigDecimal(subtotal);
            } catch (NumberFormatException e) {
                return "{\"success\": false, \"message\": \"Invalid subtotal format\"}";
            }
            
            // Save payment details to customer_payment table
            CustomerPayment paymentDetails = new CustomerPayment(
                customer.getUsername(),
                bankName.trim(),
                accountHolderName.trim(),
                accountNumber.trim()
            );
            
            customerPaymentRepository.save(paymentDetails);
            
            // Create and save order to payment table
            Order order = new Order(
                customer.getUsername(),
                productIds.trim(),
                productQuantities.trim(),
                subtotalAmount,
                payslipLocationPath != null ? payslipLocationPath.trim() : null
            );
            
            Order savedOrder = orderRepository.save(order);
            
            return "{\"success\": true, \"message\": \"Order placed successfully\", \"paymentId\": " + savedOrder.getPaymentId() + "}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Failed to place order\"}";
        }
    }
    
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
        
        // Convert product IDs to product names for each order
        for (Order order : orders) {
            String productNames = convertProductIdsToNames(order.getProductIds());
            order.setProductIds(productNames); // Reuse the field to store names instead of IDs
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
    
    @GetMapping("/get-customer-payment-details")
    @ResponseBody
    public String getCustomerPaymentDetails(HttpSession session) {
        try {
            // Check if user is logged in
            Customer customer = (Customer) session.getAttribute("customer");
            if (customer == null) {
                return "{\"success\": false, \"message\": \"User not logged in\"}";
            }
            
            // Get latest payment details for the customer
            Optional<CustomerPayment> paymentDetails = customerPaymentRepository.findFirstByUsernameAndIsActiveTrueOrderByCreatedAtDesc(customer.getUsername());
            
            if (!paymentDetails.isPresent()) {
                return "{\"success\": true, \"message\": \"No payment details found\", \"data\": null}";
            }
            
            CustomerPayment latestPayment = paymentDetails.get();
            
            return "{\"success\": true, \"message\": \"Payment details retrieved\", \"data\": {" +
                   "\"bankName\": \"" + latestPayment.getBankName() + "\"," +
                   "\"accountHolderName\": \"" + latestPayment.getAccountHolderName() + "\"," +
                   "\"accountNumber\": \"" + latestPayment.getAccountNumber() + "\"" +
                   "}}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Failed to retrieve payment details\"}";
        }
    }
}
