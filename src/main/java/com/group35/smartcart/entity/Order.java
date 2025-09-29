package com.group35.smartcart.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;
    
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must not exceed 50 characters")
    @Column(name = "username", nullable = false)
    private String username;
    
    @NotBlank(message = "Product IDs are required")
    @Column(name = "product_ids", nullable = false, columnDefinition = "TEXT")
    private String productIds;
    
    @NotBlank(message = "Product quantities are required")
    @Column(name = "product_quantities", nullable = false, columnDefinition = "TEXT")
    private String productQuantities;
    
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", message = "Subtotal must be positive")
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "payslip_location_path")
    private String payslipLocationPath;
    
    @Column(name = "order_status")
    private String orderStatus = "PENDING";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Order() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Order(String username, String productIds, String productQuantities, 
                 BigDecimal subtotal, String payslipLocationPath) {
        this();
        this.username = username;
        this.productIds = productIds;
        this.productQuantities = productQuantities;
        this.subtotal = subtotal;
        this.payslipLocationPath = payslipLocationPath;
    }
    
    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getProductIds() {
        return productIds;
    }
    
    public void setProductIds(String productIds) {
        this.productIds = productIds;
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
    
    public String getPayslipLocationPath() {
        return payslipLocationPath;
    }
    
    public void setPayslipLocationPath(String payslipLocationPath) {
        this.payslipLocationPath = payslipLocationPath;
    }
    
    public String getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
