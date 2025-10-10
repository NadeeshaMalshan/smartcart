package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {
    
    @GetMapping("/cart")
    public String cartPage(Model model, HttpSession session) {
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        model.addAttribute("title", "SmartCart - Shopping Cart");
        model.addAttribute("customer", customer);
        model.addAttribute("isLoggedIn", customer != null);
        return "cart";
    }
}
