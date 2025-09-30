package com.group35.smartcart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "product_names", nullable = false, columnDefinition = "TEXT")
    private String productNames;
    
    @Column(name = "product_quantities", nullable = false, columnDefinition = "TEXT")
    private String productQuantities;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "bank_name", nullable = false)
    private String bankName;
    
    @Column(name = "bank_account_number", nullable = false)
    private String bankAccountNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public Bill() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Bill(Long paymentId, Long orderId, String username, String productNames, 
                String productQuantities, BigDecimal subtotal, BigDecimal total, 
                String bankName, String bankAccountNumber) {
        this();
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.username = username;
        this.productNames = productNames;
        this.productQuantities = productQuantities;
        this.subtotal = subtotal;
        this.total = total;
        this.bankName = bankName;
        this.bankAccountNumber = bankAccountNumber;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getProductNames() {
        return productNames;
    }
    
    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }
    
    public String getProductQuantities() {
        return productQuantities;
    }
    
    public void setProductQuantities(String productQuantities) {
        this.productQuantities = productQuantities;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public String getBankAccountNumber() {
        return bankAccountNumber;
    }
    
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper method to get masked account number (last 4 digits only)
    public String getMaskedAccountNumber() {
        if (bankAccountNumber == null || bankAccountNumber.length() <= 4) {
            return bankAccountNumber;
        }
        return "****" + bankAccountNumber.substring(bankAccountNumber.length() - 4);
    }
}
