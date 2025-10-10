package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BillingController {
    
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
}
