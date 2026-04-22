package com.ecommerce.analytics_backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.analytics_backend.dto.MonthlyRevenueProjection;
import com.ecommerce.analytics_backend.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT SUM(o.grandTotal) FROM Order o")
    BigDecimal calculateTotalRevenue();

    // Replaced DTO with Projection and added aliases
    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%b') as month, SUM(o.grandTotal) as amount " +
           "FROM Order o " +
           "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'), FUNCTION('DATE_FORMAT', o.createdAt, '%b') " +
           "ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')")
    List<MonthlyRevenueProjection> getMonthlyRevenue();
}