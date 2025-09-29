package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Customer;
import com.group35.smartcart.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    // Login Page
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "SmartCart - Login");
        model.addAttribute("customer", new Customer());
        return "login";
    }
    
    // Login Process
    @PostMapping("/login")
    public String loginProcess(@RequestParam String username, 
                              @RequestParam String password,
                              @RequestParam(required = false) String returnUrl,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        
        System.out.println("Login attempt for username: " + username);
        
        Optional<Customer> customerOpt = customerRepository.findByUsernameAndIsActiveTrue(username);
        
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            System.out.println("Customer found: " + customer.getFirstName() + " " + customer.getLastName());
            System.out.println("Stored password: " + customer.getPassword());
            System.out.println("Entered password: " + password);
            
            // Check password (in real app, use BCryptPasswordEncoder)
            if (password.equals(customer.getPassword())) {
                session.setAttribute("customer", customer);
                redirectAttributes.addFlashAttribute("success", "Welcome back, " + customer.getFirstName() + "!");
                System.out.println("Login successful!");
                
                // Redirect to return URL if provided, otherwise go to shopping
                if (returnUrl != null && !returnUrl.isEmpty()) {
                    return "redirect:" + returnUrl;
                }
                return "redirect:/shopping";
            } else {
                System.out.println("Password mismatch!");
            }
        } else {
            System.out.println("Customer not found!");
        }
        
        redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        return "redirect:/login" + (returnUrl != null ? "?returnUrl=" + returnUrl : "");
    }
    
    // Signup Page
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("title", "SmartCart - Sign Up");
        model.addAttribute("customer", new Customer());
        return "signup";
    }
    
    // Signup Process
    @PostMapping("/signup")
    public String signupProcess(@Valid @ModelAttribute Customer customer,
                               @RequestParam String confirmPassword,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        
        // Check if passwords match
        if (!customer.getPassword().equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.customer", "Passwords do not match");
        }
        
        // Check if username already exists
        if (customerRepository.existsByUsername(customer.getUsername())) {
            bindingResult.rejectValue("username", "error.customer", "Username already exists");
        }
        
        // Check if email already exists
        if (customerRepository.existsByEmail(customer.getEmail())) {
            bindingResult.rejectValue("email", "error.customer", "Email already exists");
        }
        
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        
        try {
            // For now, store password as plain text (in production, use BCryptPasswordEncoder)
            // customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            
            customerRepository.save(customer);
            redirectAttributes.addFlashAttribute("success", "Account created successfully! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating account. Please try again.");
            return "redirect:/signup";
        }
    }
    
    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("customer");
        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/";
    }
}
