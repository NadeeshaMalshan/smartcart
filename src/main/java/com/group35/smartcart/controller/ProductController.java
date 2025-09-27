package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Customer;
import com.group35.smartcart.entity.Product;
import com.group35.smartcart.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/shopping")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping
    public String productsPage(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "search", required = false) String search,
            Model model, HttpSession session) {
        
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        List<Product> products;
        List<String> categories = productRepository.findAllCategories();
        
        if (search != null && !search.trim().isEmpty()) {
            products = productRepository.searchProducts(search.trim());
            model.addAttribute("searchTerm", search.trim());
        } else if (category != null && !category.isEmpty()) {
            products = productRepository.findByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else {
            products = productRepository.findAll();
        }
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("title", "SmartCart - Products");
        model.addAttribute("customer", customer);
        model.addAttribute("isLoggedIn", customer != null);
        
        return "shopping";
    }
    
    @GetMapping("/category/{categoryName}")
    public String productsByCategory(@PathVariable String categoryName, Model model, HttpSession session) {
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        List<Product> products = productRepository.findByCategory(categoryName);
        List<String> categories = productRepository.findAllCategories();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", categoryName);
        model.addAttribute("title", "SmartCart - " + categoryName);
        model.addAttribute("customer", customer);
        model.addAttribute("isLoggedIn", customer != null);
        
        return "shopping";
    }
    
    @GetMapping("/search")
    public String searchProducts(@RequestParam String q, Model model, HttpSession session) {
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        List<Product> products = productRepository.searchProducts(q);
        List<String> categories = productRepository.findAllCategories();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("searchTerm", q);
        model.addAttribute("title", "SmartCart - Search Results");
        model.addAttribute("customer", customer);
        model.addAttribute("isLoggedIn", customer != null);
        
        return "shopping";
    }
    
    
    @GetMapping("/product/{id}")
    public String productView(@PathVariable Long id, Model model, HttpSession session) {
        // Check if user is logged in
        Customer customer = (Customer) session.getAttribute("customer");
        
        Product product = productRepository.findById(id).orElse(null);
        
        if (product == null) {
            return "redirect:/shopping";
        }
        
        // Get related products from the same category
        List<Product> relatedProducts = productRepository.findByCategory(product.getCategory())
                .stream()
                .filter(p -> !p.getId().equals(product.getId()))
                .limit(4)
                .toList();
        
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("title", "SmartCart - " + product.getName());
        model.addAttribute("customer", customer);
        model.addAttribute("isLoggedIn", customer != null);
        
        return "productview";
    }
    
    // Get stock status for multiple products
    @PostMapping("/api/products/stock-status")
    @ResponseBody
    public Map<String, Object> getStockStatus(@RequestBody List<Long> productIds) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<Long, Map<String, Object>> stockStatus = new HashMap<>();
            
            for (Long productId : productIds) {
                Optional<Product> productOpt = productRepository.findById(productId);
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    Map<String, Object> status = new HashMap<>();
                    status.put("name", product.getName());
                    status.put("stockQuantity", product.getStockQuantity());
                    status.put("isInStock", product.isInStock());
                    status.put("status", product.isInStock() ? "Available" : "Not Available");
                    stockStatus.put(productId, status);
                } else {
                    Map<String, Object> status = new HashMap<>();
                    status.put("name", "Unknown Product");
                    status.put("stockQuantity", 0);
                    status.put("isInStock", false);
                    status.put("status", "Not Available");
                    stockStatus.put(productId, status);
                }
            }
            
            response.put("success", true);
            response.put("stockStatus", stockStatus);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to get stock status");
        }
        
        return response;
    }
}
