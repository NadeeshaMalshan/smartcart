package com.group35.smartcart.repository;

import com.group35.smartcart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find products by category
    List<Product> findByCategory(String category);
    
    
    
    // Find products in stock
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
    
    // Search products by name (case insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Search products by name or description (case insensitive)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> searchProducts(@Param("searchTerm") String searchTerm);
    
    // Find products by price range
    List<Product> findByPriceBetween(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
    
    // Find products by category and price range
    List<Product> findByCategoryAndPriceBetween(String category, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
    
    // Get all unique categories
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();
    
}
