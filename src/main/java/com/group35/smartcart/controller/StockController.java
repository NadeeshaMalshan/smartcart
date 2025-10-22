package com.group35.smartcart.controller;

import com.group35.smartcart.entity.Stock;
import com.group35.smartcart.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private StockRepository stockRepository;

    // Show all stocks
    @GetMapping
    public String listStocks(Model model) {
        List<Stock> stocks = stockRepository.findAll();
        model.addAttribute("stocks", stocks);
        // Flash attributes like "popupMessage" will already be available in the model
        return "list-stocks"; // template: list-stocks.html
    }

    // Show add stock form
    @GetMapping("/add")
    public String showAddStockForm(Model model) {
        model.addAttribute("stock", new Stock());
        return "add-stock"; // template: add-stock.html
    }

    // Save new stock
    @PostMapping("/add")
    public String addStock(@Valid @ModelAttribute("stock") Stock stock,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "add-stock";
        }
        stockRepository.save(stock);
        redirectAttributes.addFlashAttribute("popupMessage", "✅ Stock added successfully!");
        return "redirect:/stocks";
    }

    // Show edit stock form
    @GetMapping("/edit/{id}")
    public String editStock(@PathVariable Long id, Model model) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid stock Id: " + id));
        model.addAttribute("stock", stock);
        return "edit_stock"; // template: edit_stock.html
    }

    // Update stock
    @PostMapping("/update/{id}")
    public String updateStock(@PathVariable Long id,
                              @ModelAttribute("stock") Stock stockDetails,
                              RedirectAttributes redirectAttributes) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid stock Id: " + id));

        stock.setStockName(stockDetails.getStockName());
        stock.setQuantityPurchased(stockDetails.getQuantityPurchased());
        stock.setPurchaseDate(stockDetails.getPurchaseDate());
        stock.setExpiryDate(stockDetails.getExpiryDate());
        stock.setTotalInStore(stockDetails.getTotalInStore());

        stockRepository.save(stock);
        redirectAttributes.addFlashAttribute("popupMessage", "✏️ Stock updated successfully!");
        return "redirect:/stocks";
    }

    // Delete stock
    @GetMapping("/delete/{id}")
    public String deleteStock(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        stockRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("popupMessage", "🗑️ Stock deleted successfully!");
        return "redirect:/stocks";
    }
}
