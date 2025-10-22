package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Customer;
import com.group35.smartcart.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserSettingsController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/user-settings")
    public String userSettings(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        
        if (customer == null) {
            return "redirect:/login";
        }
        
        // Refresh customer data from database
        Optional<Customer> customerOpt = customerRepository.findByUsername(customer.getUsername());
        if (customerOpt.isPresent()) {
            customer = customerOpt.get();
            session.setAttribute("customer", customer);
        }
        
        model.addAttribute("customer", customer);
        model.addAttribute("title", "User Settings - SmartCart");
        
        return "user-settings";
    }

    @PostMapping("/api/user/update-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Customer customer = (Customer) session.getAttribute("customer");
        
        if (customer == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        // Validate passwords
        if (!customer.getPassword().equals(currentPassword)) {
            response.put("success", false);
            response.put("message", "Current password is incorrect");
            return ResponseEntity.ok(response);
        }
        
        if (newPassword.length() < 6) {
            response.put("success", false);
            response.put("message", "New password must be at least 6 characters");
            return ResponseEntity.ok(response);
        }
        
        if (!newPassword.equals(confirmPassword)) {
            response.put("success", false);
            response.put("message", "New passwords do not match");
            return ResponseEntity.ok(response);
        }
        
        // Update password
        customer.setPassword(newPassword);
        customerRepository.save(customer);
        session.setAttribute("customer", customer);
        
        response.put("success", true);
        response.put("message", "Password updated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/user/update-phone")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePhone(
            @RequestParam("phoneNumber") String phoneNumber,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Customer customer = (Customer) session.getAttribute("customer");
        
        if (customer == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        // Validate phone number
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Phone number is required");
            return ResponseEntity.ok(response);
        }
        
        if (phoneNumber.length() > 20) {
            response.put("success", false);
            response.put("message", "Phone number must not exceed 20 characters");
            return ResponseEntity.ok(response);
        }
        
        // Update phone number
        customer.setPhoneNumber(phoneNumber);
        customerRepository.save(customer);
        session.setAttribute("customer", customer);
        
        response.put("success", true);
        response.put("message", "Phone number updated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/user/update-address")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAddress(
            @RequestParam("billingAddress") String billingAddress,
            @RequestParam("postalCode") String postalCode,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Customer customer = (Customer) session.getAttribute("customer");
        
        if (customer == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        // Validate address
        if (billingAddress != null && billingAddress.length() > 255) {
            response.put("success", false);
            response.put("message", "Address must not exceed 255 characters");
            return ResponseEntity.ok(response);
        }
        
        if (postalCode != null && postalCode.length() > 20) {
            response.put("success", false);
            response.put("message", "Postal code must not exceed 20 characters");
            return ResponseEntity.ok(response);
        }
        
        // Update address
        customer.setBillingAddress(billingAddress);
        customer.setPostalCode(postalCode);
        customerRepository.save(customer);
        session.setAttribute("customer", customer);
        
        response.put("success", true);
        response.put("message", "Address updated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/user/delete-account")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteAccount(
            @RequestParam("password") String password,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Customer customer = (Customer) session.getAttribute("customer");
        
        if (customer == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        // Verify password
        if (!customer.getPassword().equals(password)) {
            response.put("success", false);
            response.put("message", "Incorrect password");
            return ResponseEntity.ok(response);
        }
        
        // Delete customer account
        customerRepository.delete(customer);
        session.invalidate();
        
        response.put("success", true);
        response.put("message", "Account deleted successfully");
        return ResponseEntity.ok(response);
    }
}
