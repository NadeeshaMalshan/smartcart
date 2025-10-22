package com.group35.smartcart.pattern;

import com.group35.smartcart.entity.Order;

//bill interface

public interface Bill {
    
    //methods
    String generate(Order order);
    

    String getBillType();
    

    boolean validateOrder(Order order);
}

