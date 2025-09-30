package com.group35.smartcart.service;

import com.group35.smartcart.entity.Bill;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {
    
    public byte[] generateBillPdf(Bill bill) throws IOException {
        String html = generateBillHtml(bill);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, outputStream);
        
        return outputStream.toByteArray();
    }
    
    private String generateBillHtml(Bill bill) {
        String formattedDate = bill.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"));
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Bill #%d</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 20px;
                        background-color: #f5f5f5;
                    }
                    .bill-container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        border-bottom: 3px solid #16a34a;
                        padding-bottom: 20px;
                        margin-bottom: 30px;
                    }
                    .company-name {
                        font-size: 28px;
                        font-weight: bold;
                        color: #16a34a;
                        margin-bottom: 5px;
                    }
                    .bill-title {
                        font-size: 18px;
                        color: #666;
                        margin-bottom: 10px;
                    }
                    .bill-info {
                        display: flex;
                        justify-content: space-between;
                        margin-bottom: 30px;
                        flex-wrap: wrap;
                    }
                    .info-section {
                        flex: 1;
                        min-width: 200px;
                        margin-bottom: 15px;
                    }
                    .info-label {
                        font-weight: bold;
                        color: #333;
                        margin-bottom: 5px;
                    }
                    .info-value {
                        color: #666;
                        font-family: monospace;
                    }
                    .items-section {
                        margin-bottom: 30px;
                    }
                    .section-title {
                        font-size: 16px;
                        font-weight: bold;
                        color: #333;
                        margin-bottom: 15px;
                        border-bottom: 1px solid #ddd;
                        padding-bottom: 5px;
                    }
                    .item-row {
                        display: flex;
                        justify-content: space-between;
                        padding: 8px 0;
                        border-bottom: 1px solid #f0f0f0;
                    }
                    .item-label {
                        color: #666;
                    }
                    .item-value {
                        font-weight: bold;
                        color: #333;
                    }
                    .total-section {
                        background-color: #f8f9fa;
                        padding: 20px;
                        border-radius: 5px;
                        margin-top: 20px;
                    }
                    .total-row {
                        display: flex;
                        justify-content: space-between;
                        font-size: 18px;
                        font-weight: bold;
                        color: #16a34a;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #ddd;
                        color: #666;
                        font-size: 12px;
                    }
                    .bank-info {
                        background-color: #f0f8f0;
                        padding: 15px;
                        border-radius: 5px;
                        margin-bottom: 20px;
                    }
                    .bank-title {
                        font-weight: bold;
                        color: #16a34a;
                        margin-bottom: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="bill-container">
                    <div class="header">
                        <div class="company-name">SmartCart</div>
                        <div class="bill-title">INVOICE / BILL</div>
                    </div>
                    
                    <div class="bill-info">
                        <div class="info-section">
                            <div class="info-label">Bill ID:</div>
                            <div class="info-value">#%d</div>
                        </div>
                        <div class="info-section">
                            <div class="info-label">Payment ID:</div>
                            <div class="info-value">#%d</div>
                        </div>
                        <div class="info-section">
                            <div class="info-label">Customer:</div>
                            <div class="info-value">%s</div>
                        </div>
                        <div class="info-section">
                            <div class="info-label">Date:</div>
                            <div class="info-value">%s</div>
                        </div>
                    </div>
                    
                    <div class="bank-info">
                        <div class="bank-title">Payment Information</div>
                        <div class="item-row">
                            <span class="item-label">Bank Name:</span>
                            <span class="item-value">%s</span>
                        </div>
                        <div class="item-row">
                            <span class="item-label">Account Number:</span>
                            <span class="item-value">%s</span>
                        </div>
                    </div>
                    
                    <div class="items-section">
                        <div class="section-title">Order Details</div>
                        <div class="item-row">
                            <span class="item-label">Products:</span>
                            <span class="item-value">%s</span>
                        </div>
                        <div class="item-row">
                            <span class="item-label">Quantities:</span>
                            <span class="item-value">%s</span>
                        </div>
                    </div>
                    
                    <div class="total-section">
                        <div class="item-row">
                            <span class="item-label">Subtotal:</span>
                            <span class="item-value">$%.2f</span>
                        </div>
                        <div class="total-row">
                            <span>Total Amount:</span>
                            <span>$%.2f</span>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>Thank you for shopping with SmartCart!</p>
                        <p>This is an automated bill generated on %s</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                bill.getId(),
                bill.getId(),
                bill.getPaymentId(),
                bill.getUsername(),
                formattedDate,
                bill.getBankName(),
                bill.getMaskedAccountNumber(),
                bill.getProductNames(),
                bill.getProductQuantities(),
                bill.getSubtotal().doubleValue(),
                bill.getTotal().doubleValue(),
                formattedDate
            );
    }
}
