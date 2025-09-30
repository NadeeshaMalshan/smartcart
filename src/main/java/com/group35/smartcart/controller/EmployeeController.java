package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Employee;
import com.group35.smartcart.entity.Order;
import com.group35.smartcart.entity.Product;
import com.group35.smartcart.entity.CustomerPayment;
import com.group35.smartcart.repository.OrderRepository;
import com.group35.smartcart.repository.ProductRepository;
import com.group35.smartcart.repository.CustomerPaymentRepository;
import com.group35.smartcart.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;
    
    // Employee Login Page
    @GetMapping("/employee/login")
    public String employeeLoginPage(Model model) {
        model.addAttribute("title", "SmartCart - Employee Login");
        model.addAttribute("employeeTypes", Employee.EmployeeType.values());
        return "employee-login";
    }
    
    // Employee Login Process
    @PostMapping("/employee/login")
    public String employeeLoginProcess(@RequestParam String empid, 
                                      @RequestParam String password,
                                      @RequestParam String type,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        
        System.out.println("Employee login attempt for empid: " + empid + ", type: " + type);
        
        Optional<Employee> employeeOpt = employeeService.authenticateEmployee(empid, password);
        
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            
            // Check if the employee type matches
            if (employee.getType().name().equals(type)) {
                session.setAttribute("employee", employee);
                session.setAttribute("employeeType", employee.getType().getDisplayName());
                
                redirectAttributes.addFlashAttribute("success", 
                    "Welcome, " + employee.getEmpid() + " (" + employee.getType().getDisplayName() + ")!");
                
                System.out.println("Employee login successful!");
                
                // Redirect based on employee type
                return redirectBasedOnEmployeeType(employee.getType());
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    "Employee type mismatch. Please select the correct employee type.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid employee ID or password");
        }
        
        return "redirect:/employee/login";
    }
    
    // Employee Logout
    @GetMapping("/employee/logout")
    public String employeeLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("employee");
        session.removeAttribute("employeeType");
        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/employee/login";
    }
    
    // Dashboard based on employee type
    @GetMapping("/employee/dashboard")
    public String employeeDashboard(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("employee");
        
        if (employee == null) {
            return "redirect:/employee/login";
        }
        
        model.addAttribute("employee", employee);
        model.addAttribute("title", "Employee Dashboard - " + employee.getType().getDisplayName());
        
        return "employee-dashboard";
    }
    
    // Individual dashboard routes for each employee type
    @GetMapping("/employee/cashier-dashboard")
    public String cashierDashboard(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("employee");
        
        if (employee == null || employee.getType() != Employee.EmployeeType.CASHIER) {
            return "redirect:/employee/login";
        }
        
        model.addAttribute("employee", employee);
        return "cashier-dashboard";
    }
    
    @GetMapping("/employee/store-manager-dashboard")
    public String storeManagerDashboard(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("employee");
        
        if (employee == null || employee.getType() != Employee.EmployeeType.STORE_MANAGER) {
            return "redirect:/employee/login";
        }
        
        model.addAttribute("employee", employee);
        return "store-manager-dashboard";
    }
    
    @GetMapping("/employee/it-assistant-dashboard")
    public String itAssistantDashboard(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("employee");
        
        if (employee == null || employee.getType() != Employee.EmployeeType.IT_ASSISTANT) {
            return "redirect:/employee/login";
        }
        
        model.addAttribute("employee", employee);
        return "it-assistant-dashboard";
    }
    
    @GetMapping("/employee/delivery-coordinator-dashboard")
    public String deliveryCoordinatorDashboard(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("employee");
        
        if (employee == null || employee.getType() != Employee.EmployeeType.DELIVERY_COORDINATOR) {
            return "redirect:/employee/login";
        }
        
        model.addAttribute("employee", employee);
        return "delivery-coordinator-dashboard";
    }
    
    private String redirectBasedOnEmployeeType(Employee.EmployeeType type) {
        switch (type) {
            case CASHIER:
                return "redirect:/employee/cashier-dashboard";
            case STORE_MANAGER:
                return "redirect:/employee/store-manager-dashboard";
            case IT_ASSISTANT:
                return "redirect:/employee/it-assistant-dashboard";
            case DELIVERY_COORDINATOR:
                return "redirect:/employee/delivery-coordinator-dashboard";
            default:
                return "redirect:/employee/dashboard";
        }
    }
    
    // API endpoint to fetch all payments with product details
    @GetMapping("/api/payments")
    @ResponseBody
    public Map<String, Object> getAllPayments(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null || employee.getType() != Employee.EmployeeType.CASHIER) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            return response;
        }
        
        try {
            List<Order> orders = orderRepository.findAll();
            List<Map<String, Object>> paymentCards = new ArrayList<>();
            
            for (Order order : orders) {
                Map<String, Object> paymentCard = new HashMap<>();
                paymentCard.put("paymentId", order.getPaymentId());
                paymentCard.put("username", order.getUsername());
                paymentCard.put("subtotal", order.getSubtotal());
                paymentCard.put("orderStatus", order.getOrderStatus());
                paymentCard.put("createdAt", order.getCreatedAt());
                
                // Parse product IDs and quantities
                List<Map<String, Object>> items = new ArrayList<>();
                String[] productIdStrings = order.getProductIds().split(",");
                String[] quantityStrings = order.getProductQuantities().split(",");
                
                for (int i = 0; i < productIdStrings.length; i++) {
                    try {
                        Long productId = Long.parseLong(productIdStrings[i].trim());
                        Integer quantity = Integer.parseInt(quantityStrings[i].trim());
                        
                        Optional<Product> productOpt = productRepository.findById(productId);
                        if (productOpt.isPresent()) {
                            Product product = productOpt.get();
                            Map<String, Object> item = new HashMap<>();
                            item.put("productId", productId);
                            item.put("name", product.getName());
                            item.put("price", product.getPrice());
                            item.put("quantity", quantity);
                            item.put("subtotal", product.getPrice().multiply(BigDecimal.valueOf(quantity)));
                            items.add(item);
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid entries
                    }
                }
                
                paymentCard.put("items", items);
                
                // Get customer payment details
                Optional<CustomerPayment> customerPaymentOpt = customerPaymentRepository
                    .findFirstByUsernameAndIsActiveTrueOrderByCreatedAtDesc(order.getUsername());
                
                if (customerPaymentOpt.isPresent()) {
                    CustomerPayment customerPayment = customerPaymentOpt.get();
                    Map<String, Object> paymentDetails = new HashMap<>();
                    paymentDetails.put("bankName", customerPayment.getBankName());
                    paymentDetails.put("accountHolderName", customerPayment.getAccountHolderName());
                    paymentDetails.put("accountNumber", customerPayment.getAccountNumber());
                    paymentCard.put("customerPaymentDetails", paymentDetails);
                }
                
                // Add payment slip path
                paymentCard.put("payslipLocationPath", order.getPayslipLocationPath());
                
                paymentCards.add(paymentCard);
            }
            
            // Sort by creation date (newest first)
            paymentCards.sort((a, b) -> {
                LocalDateTime dateA = (LocalDateTime) a.get("createdAt");
                LocalDateTime dateB = (LocalDateTime) b.get("createdAt");
                return dateB.compareTo(dateA);
            });
            
            response.put("success", true);
            response.put("payments", paymentCards);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to fetch payments");
        }
        
        return response;
    }
    
    // Update payment status
    @PostMapping("/api/payments/{paymentId}/status")
    @ResponseBody
    public Map<String, Object> updatePaymentStatus(@PathVariable Long paymentId, 
                                                   @RequestParam String status, 
                                                   HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null || employee.getType() != Employee.EmployeeType.CASHIER) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            return response;
        }
        
        // Validate status
        if (!Arrays.asList("PENDING", "APPROVED", "DECLINED").contains(status.toUpperCase())) {
            response.put("success", false);
            response.put("message", "Invalid status. Must be PENDING, APPROVED, or DECLINED");
            return response;
        }
        
        try {
            Optional<Order> orderOpt = orderRepository.findById(paymentId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                
                // Stock validation: If trying to approve and any product has zero stock, prevent approval
                if ("APPROVED".equals(status.toUpperCase())) {
                    String stockValidationResult = validateStockAvailability(order);
                    if (stockValidationResult != null) {
                        response.put("success", false);
                        response.put("message", stockValidationResult);
                        return response;
                    }
                }
                
                order.setOrderStatus(status.toUpperCase());
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
                
                response.put("success", true);
                response.put("message", "Payment status updated successfully");
                response.put("newStatus", status.toUpperCase());
            } else {
                response.put("success", false);
                response.put("message", "Payment not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to update payment status");
        }
        
        return response;
    }
    
    /**
     * Validates stock availability for all products in an order
     * @param order The order to validate
     * @return null if validation passes, error message if validation fails
     */
    private String validateStockAvailability(Order order) {
        try {
            String productIds = order.getProductIds();
            String productQuantities = order.getProductQuantities();
            
            if (productIds == null || productIds.trim().isEmpty() || 
                productQuantities == null || productQuantities.trim().isEmpty()) {
                return "Invalid order data";
            }
            
            String[] ids = productIds.split(",");
            String[] quantities = productQuantities.split(",");
            
            if (ids.length != quantities.length) {
                return "Mismatch between product IDs and quantities";
            }
            
            for (int i = 0; i < ids.length; i++) {
                try {
                    Long productId = Long.parseLong(ids[i].trim());
                    int requestedQuantity = Integer.parseInt(quantities[i].trim());
                    
                    Optional<Product> productOpt = productRepository.findById(productId);
                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        if (product.getStockQuantity() == null || product.getStockQuantity() <= 0) {
                            return "Cannot approve payment: Product '" + product.getName() + "' is out of stock (quantity: 0)";
                        }
                        if (product.getStockQuantity() < requestedQuantity) {
                            return "Cannot approve payment: Insufficient stock for product '" + product.getName() + 
                                   "' (requested: " + requestedQuantity + ", available: " + product.getStockQuantity() + ")";
                        }
                    } else {
                        return "Product with ID " + productId + " not found";
                    }
                } catch (NumberFormatException e) {
                    return "Invalid product ID or quantity format";
                }
            }
            
            return null; // Validation passed
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error validating stock availability";
        }
    }
    
    // Check stock availability for a payment
    @GetMapping("/api/payments/{paymentId}/stock-check")
    @ResponseBody
    public Map<String, Object> checkStockAvailability(@PathVariable Long paymentId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null || employee.getType() != Employee.EmployeeType.CASHIER) {
            response.put("success", false);
            response.put("message", "Unauthorized access");
            return response;
        }
        
        try {
            Optional<Order> orderOpt = orderRepository.findById(paymentId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                String stockValidationResult = validateStockAvailability(order);
                
                if (stockValidationResult == null) {
                    response.put("success", true);
                    response.put("canApprove", true);
                    response.put("message", "All products have sufficient stock");
                } else {
                    response.put("success", true);
                    response.put("canApprove", false);
                    response.put("message", stockValidationResult);
                }
            } else {
                response.put("success", false);
                response.put("message", "Payment not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to check stock availability");
        }
        
        return response;
    }
    
    // Serve payment slip files
    @GetMapping("/payslips/{filename:.+}")
    public ResponseEntity<Resource> servePaymentSlip(@PathVariable String filename, HttpServletRequest request) {
        try {
            Employee employee = (Employee) request.getSession().getAttribute("employee");
            if (employee == null || employee.getType() != Employee.EmployeeType.CASHIER) {
                return ResponseEntity.status(403).build();
            }
            
            Path filePath = Paths.get("src/main/resources/payslips").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                // Determine content type
                String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
