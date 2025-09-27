package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        model.addAttribute("title", "SmartCart - Your Smart Supermarket");
        model.addAttribute("welcomeMessage", "Welcome to SmartCart");
        model.addAttribute("subtitle", "Shop smart, shop easy!");
        model.addAttribute("customer", customer);
        model.addAttribute("isLoggedIn", customer != null);
        return "index";
    }

    @GetMapping("/home")
    public String homePage(Model model, HttpSession session) {
        return home(model, session);
    }
}
