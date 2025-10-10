package com.group35.smartcart.service;

import com.group35.smartcart.entity.Employee;
import com.group35.smartcart.entity.Product;
import com.group35.smartcart.repository.EmployeeRepository;
import com.group35.smartcart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializationService implements CommandLineRunner {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Value("${app.initialize-data:true}")
    private boolean initializeData;
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if the flag is enabled
        if (!initializeData) {
            System.out.println("Data initialization is disabled via configuration");
            return;
        }
        
        // Only initialize if no products exist
        if (productRepository.count() == 0) {
            System.out.println("No products found, initializing sample products...");
            initializeProducts();
        } else {
            System.out.println("Products already exist, skipping initialization");
        }
        
        // Only initialize if no employees exist
        if (employeeRepository.count() == 0) {
            System.out.println("No employees found, initializing sample employees...");
            initializeEmployees();
        } else {
            System.out.println("Employees already exist, skipping initialization");
        }
    }
    
    private void initializeProducts() {
        List<Product> products = Arrays.asList(
            // Fresh Produce
            new Product("Fresh Tomatoes", "Organic vine-ripened tomatoes", new BigDecimal("2.90"), 
                       "https://images.unsplash.com/photo-1546470427-5c4b4b4b4b4b?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Fresh Produce"),
            new Product("Green Broccoli", "Fresh broccoli florets", new BigDecimal("1.50"), 
                       "https://images.unsplash.com/photo-1584270354949-c26b0cf5d4a8?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Fresh Produce"),
            new Product("Fresh Spinach", "Organic baby spinach leaves", new BigDecimal("3.90"), 
                       "https://images.unsplash.com/photo-1576045057995-568f588f82fb?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Fresh Produce"),
            new Product("Cherry Tomatoes", "Sweet cherry tomatoes", new BigDecimal("2.50"), 
                       "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Fresh Produce"),
            new Product("Mixed Nuts", "Premium mixed nuts", new BigDecimal("4.50"), 
                       "https://images.unsplash.com/photo-1551698618-1dfe5d97d256?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Fresh Produce"),
            
            // Meat & Seafood
            new Product("Fresh Salmon", "Atlantic salmon fillet", new BigDecimal("12.99"), 
                       "https://images.unsplash.com/photo-1519708227418-c8fd9a802b9b?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Meat & Seafood"),
            new Product("Chicken Breast", "Free-range chicken breast", new BigDecimal("8.99"), 
                       "https://images.unsplash.com/photo-1604503468506-a8da13d82791?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Meat & Seafood"),
            new Product("Ground Beef", "Premium ground beef", new BigDecimal("6.99"), 
                       "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Meat & Seafood"),
            
            // Bakery
            new Product("Artisan Bread", "Fresh baked artisan bread", new BigDecimal("3.99"), 
                       "https://images.unsplash.com/photo-1509440159596-0249088772ff?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Bakery"),
            new Product("Croissants", "Buttery French croissants", new BigDecimal("4.50"), 
                       "https://images.unsplash.com/photo-1555507036-ab1f4038808a?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Bakery"),
            new Product("Chocolate Cake", "Rich chocolate cake", new BigDecimal("15.99"), 
                       "https://images.unsplash.com/photo-1578985545062-69928b1d9587?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Bakery"),
            
            // Dairy
            new Product("Fresh Milk", "Whole milk 1 gallon", new BigDecimal("3.49"), 
                       "https://images.unsplash.com/photo-1550583724-b2692b85b150?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Dairy"),
            new Product("Greek Yogurt", "Creamy Greek yogurt", new BigDecimal("4.99"), 
                       "https://images.unsplash.com/photo-1571212053456-5d5b0b5b5b5b?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Dairy"),
            new Product("Cheddar Cheese", "Aged cheddar cheese", new BigDecimal("5.99"), 
                       "https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Dairy"),
            new Product("Free Range Eggs", "Farm fresh eggs", new BigDecimal("3.99"), 
                       "https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Dairy"),
            
            // Beverages
            new Product("Orange Juice", "Fresh squeezed orange juice", new BigDecimal("4.99"), 
                       "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Beverages"),
            new Product("Green Tea", "Premium green tea", new BigDecimal("2.99"), 
                       "https://images.unsplash.com/photo-1556679343-c7306c1976bc?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Beverages"),
            new Product("Coffee Beans", "Arabica coffee beans", new BigDecimal("8.99"), 
                       "https://images.unsplash.com/photo-1559056199-641a0ac8b55c?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Beverages"),
            
            // Snacks
            new Product("Organic Chips", "Sea salt potato chips", new BigDecimal("3.99"), 
                       "https://images.unsplash.com/photo-1566478989037-eec170784d0b?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Snacks"),
            new Product("Dark Chocolate", "Premium dark chocolate", new BigDecimal("6.99"), 
                       "https://images.unsplash.com/photo-1511381939415-e44015466834?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&q=80", "Snacks")
        );
        
        // Set stock quantities for all products
        for (int i = 0; i < products.size(); i++) {
            products.get(i).setStockQuantity(15 + (i % 20));
        }
        
        productRepository.saveAll(products);
        System.out.println("Initialized " + products.size() + " products in the database");
    }
    
    private void initializeEmployees() {
        List<Employee> employees = Arrays.asList(
            // Cashier employees
            new Employee("EMP001", "password123", Employee.EmployeeType.CASHIER),
            new Employee("EMP002", "password123", Employee.EmployeeType.CASHIER),
            
            // Store Manager
            new Employee("MGR001", "manager123", Employee.EmployeeType.STORE_MANAGER),
            
            // IT Assistant
            new Employee("IT001", "itadmin123", Employee.EmployeeType.IT_ASSISTANT),
            
            // Delivery Coordinator
            new Employee("DEL001", "delivery123", Employee.EmployeeType.DELIVERY_COORDINATOR)
        );
        
        employeeRepository.saveAll(employees);
        System.out.println("Initialized " + employees.size() + " employees in the database");
    }
}
