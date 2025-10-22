package com.group35.smartcart.pattern;

import com.group35.smartcart.entity.Order;
import org.springframework.stereotype.Component;


//concrete class printed bill
public class PrintedBill implements Bill {
    //constant
    private static final String BILL_TYPE = "PRINTED";
    
    //order method
    @Override
    public String generate(Order order) {
        //check order is valid
        if (!validateOrder(order)) {
            return "Failed to generate printed bill: Invalid order data";
        }
        
        try {
            String billContent = formatBillForPrinting(order);

            System.out.println("LOGIC EXECUTED: Printed bill request processed for Order ID: " + order.getPaymentId() + " (IT IS WORKING!!! PRINTED BILL GENERATED SUCCESSFULLY)");
            
            logPrintRequest(order);
            
            return "Printed bill for order #" + order.getPaymentId() + " has been queued for printing.";
            
        } catch (Exception e) {
            return "Failed to generate printed bill: " + e.getMessage();
        }
    }
    
    @Override
    //bill type
    public String getBillType() {
        return BILL_TYPE;
    }
    
    @Override
    public boolean validateOrder(Order order) {
        if (order == null) {
            return false;
        }
        
        if (order.getPaymentId() == null || order.getUsername() == null || 
            order.getUsername().trim().isEmpty()) {
            return false;
        }
        
        if (order.getSubtotal() == null || order.getSubtotal().doubleValue() <= 0) {
            return false;
        }
        
        return true;
    }
    

    
   //output of bill
    private String formatBillForPrinting(Order order) {
        StringBuilder billContent = new StringBuilder();
        
        // Header
        billContent.append("========================================\n");
        billContent.append("           SMART CART RECEIPT          \n");
        billContent.append("========================================\n");
        billContent.append("Order ID: ").append(order.getPaymentId()).append("\n");
        billContent.append("Date: ").append(order.getCreatedAt()).append("\n");
        billContent.append("Customer: ").append(order.getUsername()).append("\n");
    
        billContent.append("----------------------------------------\n");
        
        // Order Details
        billContent.append("ORDER DETAILS:\n");
        billContent.append("Product IDs: ").append(order.getProductIds()).append("\n");
        billContent.append("Quantities: ").append(order.getProductQuantities()).append("\n");
        billContent.append("----------------------------------------\n");
        
        // Financial Summary
        billContent.append("FINANCIAL SUMMARY:\n");
        billContent.append("Subtotal: LKR ").append(order.getSubtotal()).append("\n");
        
        // Calculate tax (15%)
        double tax = order.getSubtotal().doubleValue() * 0.15;
        billContent.append("Tax (15%): LKR ").append(String.format("%.2f", tax)).append("\n");
        
        double total = order.getSubtotal().doubleValue() + tax;
        billContent.append("TOTAL: LKR ").append(String.format("%.2f", total)).append("\n");
        billContent.append("----------------------------------------\n");
        
        // Payment Information
        if (order.getPayslipLocationPath() != null && !order.getPayslipLocationPath().isEmpty()) {
            billContent.append("Payment: Bank Transfer\n");
            billContent.append("Payslip: ").append(order.getPayslipLocationPath()).append("\n");
        }
        
        billContent.append("Status: ").append(order.getOrderStatus()).append("\n");
        billContent.append("========================================\n");
        billContent.append("Thank you for shopping with SmartCart!\n");
        billContent.append("========================================\n");
        
        return billContent.toString();
    }
    
    
    private void logPrintRequest(Order order) {
        // In a real implementation, this would:
        // 1. Send to print queue management system
        // 2. Update order status to "PRINTED"
        // 3. Send notification to customer
        // 4. Log for audit purposes
        
        System.out.println("Print request logged for Order #" + order.getPaymentId() + 
                          " at " + java.time.LocalDateTime.now());
    }
}
