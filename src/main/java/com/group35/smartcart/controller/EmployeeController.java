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

    @GetMapping("/deliver-person-management")
    public String deliveryPersonManagement(){
        return "deliver-person-management";
    }

    @GetMapping("/deliver-assigment-management")
    public String deliveryAssigmentManagement(){
        return "deliver-assigment-management";
    }

    @GetMapping("/admin-reviews")
    public String adminReviews(Model model, HttpSession session){

        model.addAttribute("title", "SmartCart - Admin Reviews");
        return "admin-reviews";
    }

}
