package com.group35.smartcart.pattern;

import com.group35.smartcart.entity.Order;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


// downloadable bill concrete class
@Component
public class DownloadableBill implements Bill {
    
    private static final String BILL_TYPE = "DOWNLOADABLE";
    private static final String BILLS_DIRECTORY = "src/main/resources/bills/";
    
    @Override
    public String generate(Order order) {
        if (!validateOrder(order)) {
            return "Failed to generate downloadable bill: Invalid order data";
        }
        
        try {
            // Create bills directory if it doesn't exist
            createBillsDirectory();
            
            // Generate bill content
            String billContent = formatBillForDownload(order);
            
            // Save bill to file
            String fileName = generateFileName(order);
            String filePath = BILLS_DIRECTORY + fileName;
            
            saveBillToFile(billContent, filePath);
            
            System.out.println("LOGIC EXECUTED: bill request processed for Order ID: " + order.getPaymentId() + " (IT IS WORKING!!! BILL GENERATED SUCCESSFULLY)");
            
            System.out.println("=== BILL GENERATION ===");
            System.out.println("Order ID: " + order.getPaymentId());
            System.out.println("Customer: " + order.getUsername());
            System.out.println("File saved: " + filePath);
            System.out.println("=== END BILL ===");
            
            return "PDF bill for order #" + order.getPaymentId() + " is ready for download at: " + fileName;
            
        } catch (Exception e) {
            return "Failed to generate downloadable bill: " + e.getMessage();
        }
    }
    
    @Override
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
    
   
    private String formatBillForDownload(Order order) {
        StringBuilder billContent = new StringBuilder();
        
        // HTML format for better presentation (could be converted to PDF)
        billContent.append("<!DOCTYPE html>\n");
        billContent.append("<html>\n<head>\n");
        billContent.append("<title>SmartCart Receipt - Order #").append(order.getPaymentId()).append("</title>\n");
        billContent.append("<style>\n");
        billContent.append("body { font-family: Arial, sans-serif; margin: 40px; }\n");
        billContent.append(".header { text-align: center; border-bottom: 2px solid #16a34a; padding-bottom: 20px; margin-bottom: 30px; }\n");
        billContent.append(".section { margin-bottom: 25px; }\n");
        billContent.append(".total { font-weight: bold; font-size: 1.2em; color: #16a34a; }\n");
        billContent.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n");
        billContent.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n");
        billContent.append("th { background-color: #f8f9fa; }\n");
        billContent.append("</style>\n");
        billContent.append("</head>\n<body>\n");
        
        // Header
        billContent.append("<div class='header'>\n");
        billContent.append("<h1>SmartCart Receipt</h1>\n");
        billContent.append("<h2>Order #").append(order.getPaymentId()).append("</h2>\n");
        billContent.append("</div>\n");
        
        // Order Information
        billContent.append("<div class='section'>\n");
        billContent.append("<h3>Order Information</h3>\n");
        billContent.append("<table>\n");
        billContent.append("<tr><th>Order ID</th><td>").append(order.getPaymentId()).append("</td></tr>\n");
        billContent.append("<tr><th>Date</th><td>").append(order.getCreatedAt()).append("</td></tr>\n");
        billContent.append("<tr><th>Customer</th><td>").append(order.getUsername()).append("</td></tr>\n");
        billContent.append("<tr><th>Status</th><td>").append(order.getOrderStatus()).append("</td></tr>\n");
        billContent.append("</table>\n");
        billContent.append("</div>\n");
        
        // Product Details
        billContent.append("<div class='section'>\n");
        billContent.append("<h3>Product Details</h3>\n");
        billContent.append("<table>\n");
        billContent.append("<tr><th>Product IDs</th><td>").append(order.getProductIds()).append("</td></tr>\n");
        billContent.append("<tr><th>Quantities</th><td>").append(order.getProductQuantities()).append("</td></tr>\n");
        billContent.append("</table>\n");
        billContent.append("</div>\n");
        
        // Financial Summary
        billContent.append("<div class='section'>\n");
        billContent.append("<h3>Financial Summary</h3>\n");
        billContent.append("<table>\n");
        billContent.append("<tr><th>Subtotal</th><td>LKR ").append(order.getSubtotal()).append("</td></tr>\n");
        
        // Calculate tax (15%)
        double tax = order.getSubtotal().doubleValue() * 0.15;
        billContent.append("<tr><th>Tax (15%)</th><td>LKR ").append(String.format("%.2f", tax)).append("</td></tr>\n");
        
        double total = order.getSubtotal().doubleValue() + tax;
        billContent.append("<tr class='total'><th>TOTAL</th><td>LKR ").append(String.format("%.2f", total)).append("</td></tr>\n");
        billContent.append("</table>\n");
        billContent.append("</div>\n");
        
        // Payment Information
        if (order.getPayslipLocationPath() != null && !order.getPayslipLocationPath().isEmpty()) {
            billContent.append("<div class='section'>\n");
            billContent.append("<h3>Payment Information</h3>\n");
            billContent.append("<p><strong>Payment Method:</strong> Bank Transfer</p>\n");
            billContent.append("<p><strong>Payslip:</strong> ").append(order.getPayslipLocationPath()).append("</p>\n");
            billContent.append("</div>\n");
        }
        
        // Footer
        billContent.append("<div class='section'>\n");
        billContent.append("<p style='text-align: center; color: #666; margin-top: 40px;'>");
        billContent.append("Thank you for shopping with SmartCart!<br>");
        billContent.append("Generated on ").append(java.time.LocalDateTime.now()).append("</p>\n");
        billContent.append("</div>\n");
        
        billContent.append("</body>\n</html>");
        
        return billContent.toString();
    }
    
   
    private String generateFileName(Order order) {
        String timestamp = java.time.LocalDateTime.now().toString()
            .replace(":", "-").replace(".", "-");
        return "bill_order_" + order.getPaymentId() + "_" + timestamp + ".html";
    }
    
    
    private void createBillsDirectory() throws IOException {
        Path billsPath = Paths.get(BILLS_DIRECTORY);
        if (!Files.exists(billsPath)) {
            Files.createDirectories(billsPath);
        }
    }
    
    // Saves the bill content to a file.
    
    private void saveBillToFile(String content, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }
}
