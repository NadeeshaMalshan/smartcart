package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Customer;
import com.group35.smartcart.entity.CustomerPayment;
import com.group35.smartcart.entity.Order;
import com.group35.smartcart.entity.Product;
import com.group35.smartcart.entity.Bill;
import com.group35.smartcart.repository.CustomerPaymentRepository;
import com.group35.smartcart.repository.OrderRepository;
import com.group35.smartcart.repository.ProductRepository;
import com.group35.smartcart.repository.BillRepository;
import com.group35.smartcart.service.PdfService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
    
    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private PdfService pdfService;
    
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
    
    @GetMapping("/get-order-bill/{paymentId}")
    @ResponseBody
    public String getOrderBill(@PathVariable Long paymentId, HttpSession session) {
        try {
            // Check if user is logged in
            Customer customer = (Customer) session.getAttribute("customer");
            if (customer == null) {
                return "{\"success\": false, \"message\": \"User not logged in\"}";
            }
            
            // Get the order to verify ownership
            Optional<Order> orderOpt = orderRepository.findById(paymentId);
            if (!orderOpt.isPresent()) {
                return "{\"success\": false, \"message\": \"Order not found\"}";
            }
            
            Order order = orderOpt.get();
            if (!order.getUsername().equals(customer.getUsername())) {
                return "{\"success\": false, \"message\": \"Unauthorized access\"}";
            }
            
            // Check if order is approved
            if (!"APPROVED".equals(order.getOrderStatus())) {
                return "{\"success\": false, \"message\": \"Bill not available for non-approved orders\"}";
            }
            
            // Get bill information
            Optional<Bill> billOpt = billRepository.findByPaymentId(paymentId);
            if (!billOpt.isPresent()) {
                return "{\"success\": false, \"message\": \"Bill not found\"}";
            }
            
            Bill bill = billOpt.get();
            
            return "{\"success\": true, \"message\": \"Bill retrieved\", \"data\": {" +
                   "\"id\": " + bill.getId() + "," +
                   "\"paymentId\": " + bill.getPaymentId() + "," +
                   "\"productNames\": \"" + bill.getProductNames() + "\"," +
                   "\"productQuantities\": \"" + bill.getProductQuantities() + "\"," +
                   "\"subtotal\": " + bill.getSubtotal() + "," +
                   "\"total\": " + bill.getTotal() + "," +
                   "\"bankName\": \"" + bill.getBankName() + "\"," +
                   "\"maskedAccountNumber\": \"" + bill.getMaskedAccountNumber() + "\"," +
                   "\"createdAt\": \"" + bill.getCreatedAt() + "\"" +
                   "}}";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"Failed to retrieve bill\"}";
        }
    }
    
    @GetMapping("/download-bill-pdf/{paymentId}")
    public ResponseEntity<byte[]> downloadBillPdf(@PathVariable Long paymentId, HttpSession session) {
        try {
            // Check if user is logged in
            Customer customer = (Customer) session.getAttribute("customer");
            if (customer == null) {
                return ResponseEntity.status(401).build();
            }
            
            // Get the order to verify ownership
            Optional<Order> orderOpt = orderRepository.findById(paymentId);
            if (!orderOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Order order = orderOpt.get();
            if (!order.getUsername().equals(customer.getUsername())) {
                return ResponseEntity.status(403).build();
            }
            
            // Check if order is approved
            if (!"APPROVED".equals(order.getOrderStatus())) {
                return ResponseEntity.badRequest().build();
            }
            
            // Get bill information
            Optional<Bill> billOpt = billRepository.findByPaymentId(paymentId);
            if (!billOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Bill bill = billOpt.get();
            
            // Generate PDF
            byte[] pdfBytes = pdfService.generateBillPdf(bill);
            
            // Set headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bill_" + bill.getId() + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
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
