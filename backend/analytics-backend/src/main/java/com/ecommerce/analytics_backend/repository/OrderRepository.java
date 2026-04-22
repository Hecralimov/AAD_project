package com.ecommerce.analytics_backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.analytics_backend.dto.DashboardAnalyticsDto;
import com.ecommerce.analytics_backend.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT SUM(o.grandTotal) FROM Order o")
    BigDecimal getTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o")
    Long getTotalOrders();

    @Query("SELECT COUNT(DISTINCT o.userId) FROM Order o")
    Long getActiveUsersCount();

    @Query("SELECT new com.ecommerce.analytics_backend.dto.DashboardAnalyticsDto.MonthlyRevenueDto(" +
            "FUNCTION('MONTHNAME', o.createdAt), SUM(o.grandTotal)) " +
            "FROM Order o GROUP BY FUNCTION('MONTHNAME', o.createdAt), FUNCTION('MONTH', o.createdAt) " +
            "ORDER BY FUNCTION('MONTH', o.createdAt)")
    List<DashboardAnalyticsDto.MonthlyRevenueDto> getMonthlyRevenue();

    List<Order> findByUserId(String userId);

    @Query("SELECT SUM(o.grandTotal) FROM Order o WHERE o.userId = :userId")
    BigDecimal getTotalSpendByUserId(String userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    Long getOrderCountByUserId(String userId);

    @Query("SELECT SUM(o.grandTotal) FROM Order o WHERE o.storeId = :storeId")
    BigDecimal getTotalRevenueByStoreId(String storeId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.storeId = :storeId")
    Long getOrderCountByStoreId(String storeId);
}
