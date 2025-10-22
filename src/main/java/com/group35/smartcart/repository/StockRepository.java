package com.group35.smartcart.repository;

import com.group35.smartcart.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    boolean existsByStockId(String stockId);
}
