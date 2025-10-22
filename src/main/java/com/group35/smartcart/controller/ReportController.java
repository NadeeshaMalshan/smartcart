package com.group35.smartcart.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

// ✅ Use OpenPDF imports instead of iText
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

@RestController
public class ReportController {

    @PostMapping("/generate-stock-report")
    public ResponseEntity<byte[]> generateStockReport(@RequestBody Map<String, String> data) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font labelFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font valueFont = new Font(Font.HELVETICA, 12);

            document.add(new Paragraph("Stock Report", titleFont));
            document.add(new Paragraph("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Stock Details:", labelFont));
            document.add(new Paragraph("Stock ID: " + data.get("stockId"), valueFont));
            document.add(new Paragraph("Stock Name: " + data.get("stockName"), valueFont));
            document.add(new Paragraph("Available Quantity: " + data.get("available"), valueFont));
            document.add(new Paragraph("Needed Quantity: " + data.get("needed"), valueFont));
            document.add(new Paragraph("Supplier Name: " + data.get("supplierName"), valueFont));
            document.add(new Paragraph("Supplier ID: " + data.get("supplierId"), valueFont));
            document.add(new Paragraph("Description: " + data.get("description"), valueFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Signature: ________________________", valueFont));
            document.add(new Paragraph("Store Manager", valueFont));

            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Stock_Report_" + data.get("stockId") + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(outputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
