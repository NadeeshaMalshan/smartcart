package com.group35.smartcart.pattern;

import com.group35.smartcart.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


// factory class
public class BillFactory {
    
    private final Map<String, Bill> billStrategies;
    
    @Autowired
    public BillFactory(PrintedBill printedBill, DownloadableBill downloadableBill) {
        this.billStrategies = new HashMap<>();
        
        // Register available bill strategies
        this.billStrategies.put("PRINTED", printedBill);
        this.billStrategies.put("DOWNLOADABLE", downloadableBill);
    }
    
    //create bill
    public Bill createBill(String billType, Order order) {
        if (billType == null || order == null) {
            return null;
        }
        
        String normalizedBillType = billType.toUpperCase().trim();
        Bill billStrategy = billStrategies.get(normalizedBillType);
        
        if (billStrategy == null) {
            System.err.println("Unsupported bill type: " + billType);
            return null;
        }
        
        return billStrategy;
    }

    public String[] getAvailableBillTypes() {
        return billStrategies.keySet().toArray(new String[0]);
    }
    

    public boolean isBillTypeSupported(String billType) {
        if (billType == null) {
            return false;
        }
        return billStrategies.containsKey(billType.toUpperCase().trim());
    }

    public int getAvailableBillTypeCount() {
        return billStrategies.size();
    }
}

