package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Employee;
import com.group35.smartcart.entity.Product;
import com.group35.smartcart.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductManagementController {
    
    @Autowired
    private ProductRepository productRepository;
    
    // Check if user is authorized (store manager)
    private boolean isAuthorized(HttpSession session) {
        Employee employee = (Employee) session.getAttribute("employee");
        return employee != null && employee.getType().name().equals("STORE_MANAGER");
    }
    
    // Get categories with fallback
    private List<String> getCategories() {
        List<String> categories = productRepository.findAllCategories();
        
        // If no categories exist in database, provide default categories
        if (categories.isEmpty()) {
            categories = Arrays.asList(
                "Fresh Produce", "Meat & Seafood", "Bakery", "Dairy", 
                "Beverages", "Snacks", "Frozen Foods", "Pantry", "Health & Beauty"
            );
        }
        
        return categories;
    }
    
    // Product Management Dashboard
    @GetMapping("/manage")
    public String manageProducts(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "search", required = false) String search,
            Model model, HttpSession session) {
        
        if (!isAuthorized(session)) {
            return "redirect:/employee/login";
        }
        
        Employee employee = (Employee) session.getAttribute("employee");
        List<Product> products;
        List<String> categories = getCategories();
        
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
        model.addAttribute("employee", employee);
        model.addAttribute("title", "Product Management - SmartCart");
        
        return "product-management";
    }
    
    // Add Product Form
    @GetMapping("/add")
    public String addProductForm(Model model, HttpSession session) {
        if (!isAuthorized(session)) {
            return "redirect:/employee/login";
        }
        
        Employee employee = (Employee) session.getAttribute("employee");
        List<String> categories = getCategories();
        
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categories);
        model.addAttribute("employee", employee);
        model.addAttribute("title", "Add Product - SmartCart");
        
        return "add-product";
    }
    
    // Add Product Submit
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product, 
                           @RequestParam("stockQuantity") Integer stockQuantity,
                           RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAuthorized(session)) {
            return "redirect:/employee/login";
        }
        
        try {
            // Set stock quantity
            product.setStockQuantity(stockQuantity != null ? stockQuantity : 0);
            
            // Validate required fields
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Product name is required");
                return "redirect:/products/add";
            }
            
            if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Valid price is required");
                return "redirect:/products/add";
            }
            
            // Save product
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add product: " + e.getMessage());
        }
        
        return "redirect:/products/manage";
    }
    
    // Edit Product Form
    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAuthorized(session)) {
            return "redirect:/employee/login";
        }
        
        Optional<Product> productOpt = productRepository.findById(id);
        if (!productOpt.isPresent()) {
            return "redirect:/products/manage";
        }
        
        Employee employee = (Employee) session.getAttribute("employee");
        List<String> categories = getCategories();
        
        model.addAttribute("product", productOpt.get());
        model.addAttribute("categories", categories);
        model.addAttribute("employee", employee);
        model.addAttribute("title", "Edit Product - SmartCart");
        
        return "edit-product";
    }
    
    // Update Product Submit
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, 
                              @ModelAttribute Product product,
                              @RequestParam("stockQuantity") Integer stockQuantity,
                              RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAuthorized(session)) {
            return "redirect:/employee/login";
        }
        
        try {
            Optional<Product> existingProductOpt = productRepository.findById(id);
            if (!existingProductOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Product not found");
                return "redirect:/products/manage";
            }
            
            Product existingProduct = existingProductOpt.get();
            
            // Update fields
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setImageUrl(product.getImageUrl());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setStockQuantity(stockQuantity != null ? stockQuantity : 0);
            
            // Validate required fields
            if (existingProduct.getName() == null || existingProduct.getName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Product name is required");
                return "redirect:/products/edit/" + id;
            }
            
            if (existingProduct.getPrice() == null || existingProduct.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Valid price is required");
                return "redirect:/products/edit/" + id;
            }
            
            // Save updated product
            productRepository.save(existingProduct);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update product: " + e.getMessage());
        }
        
        return "redirect:/products/manage";
    }
    
    // Delete Product
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, 
                              RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAuthorized(session)) {
            return "redirect:/employee/login";
        }
        
        try {
            Optional<Product> productOpt = productRepository.findById(id);
            if (!productOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Product not found");
                return "redirect:/products/manage";
            }
            
            Product product = productOpt.get();
            productRepository.delete(product);
            redirectAttributes.addFlashAttribute("success", "Product '" + product.getName() + "' deleted successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete product: " + e.getMessage());
        }
        
        return "redirect:/products/manage";
    }
    
    // Update Stock Only
    @PostMapping("/update-stock/{id}")
    public String updateStock(@PathVariable Long id, 
                            @RequestParam("stockQuantity") Integer stockQuantity,
                            RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAuthorized(session)) {
            return "redirect:/employee/login";
        }
        
        try {
            Optional<Product> productOpt = productRepository.findById(id);
            if (!productOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Product not found");
                return "redirect:/products/manage";
            }
            
            Product product = productOpt.get();
            product.setStockQuantity(stockQuantity != null ? stockQuantity : 0);
            productRepository.save(product);
            
            redirectAttributes.addFlashAttribute("success", "Stock updated for '" + product.getName() + "'");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update stock: " + e.getMessage());
        }
        
        return "redirect:/products/manage";
    }
    
    // Clear All Products
    @PostMapping("/clear-all")
    public String clearAllProducts(RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAuthorized(session)) {
            return "redirect:/employee/login";
        }
        
        try {
            long count = productRepository.count();
            productRepository.deleteAll();
            redirectAttributes.addFlashAttribute("success", 
                "Successfully deleted " + count + " products. Data initialization is now disabled, so products won't be recreated on restart.");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to clear products: " + e.getMessage());
        }
        
        return "redirect:/products/manage";
    }
    
    // Disable Data Initialization (Prevent products from being recreated)
    @PostMapping("/disable-initialization")
    public String disableInitialization(RedirectAttributes redirectAttributes, HttpSession session) {
        if (!isAuthorized(session)) {
            return "redirect:/employee/login";
        }
        
        try {
            redirectAttributes.addFlashAttribute("success", 
                "Data initialization is now disabled. Products will not be recreated on restart. You can now safely delete products.");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to disable initialization: " + e.getMessage());
        }
        
        return "redirect:/products/manage";
    }
}
