package com.ecommerce.analytics_backend.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.analytics_backend.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT SUM(o.grandTotal) FROM Order o")
    BigDecimal calculateTotalRevenue();
}
