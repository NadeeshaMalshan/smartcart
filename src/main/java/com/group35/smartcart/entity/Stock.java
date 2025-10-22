package com.group35.smartcart.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // business stock code (unique)
    @Column(nullable = false, unique = true)
    private String stockId;

    @Column(nullable = false)
    private String stockName;

    // Use Integer to avoid primitive binding issues
    @Column(nullable = false)
    private Integer quantityPurchased;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    @Column(nullable = false)
    private Integer totalInStore;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStockId() { return stockId; }
    public void setStockId(String stockId) { this.stockId = stockId; }

    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }

    public Integer getQuantityPurchased() { return quantityPurchased; }
    public void setQuantityPurchased(Integer quantityPurchased) { this.quantityPurchased = quantityPurchased; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Integer getTotalInStore() { return totalInStore; }
    public void setTotalInStore(Integer totalInStore) { this.totalInStore = totalInStore; }
}
