package com.ecommerce.analytics_backend.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.ecommerce.analytics_backend.dto.MonthlyRevenueProjection;
import com.ecommerce.analytics_backend.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT SUM(o.grandTotal) FROM Order o")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%b') as month, SUM(o.grandTotal) as amount " +
            "FROM Order o " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'), FUNCTION('DATE_FORMAT', o.createdAt, '%b') " +
            "ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')")
    List<MonthlyRevenueProjection> getMonthlyRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.storeId = :storeId")
    Long countByStoreId(String storeId);

    @Query("SELECT SUM(o.grandTotal) FROM Order o WHERE o.storeId = :storeId")
    BigDecimal calculateTotalRevenueByStoreId(String storeId);

    @Query("SELECT SUM(o.grandTotal) FROM Order o WHERE o.userId = :userId")
    BigDecimal calculateTotalSpentByUserId(String userId);

    Long countByUserId(String userId);

    @Query("SELECT o.status as status, COUNT(o) as count FROM Order o WHERE o.userId = :userId GROUP BY o.status")
    List<Map<String, Object>> getOrderStatusDistribution(String userId);

    List<Order> findByStoreId(String storeId);

    List<Order> findByStoreIdOrderByCreatedAtDesc(String storeId);

    List<Order> findByStoreIdAndStatus(String storeId, String status);

    Optional<Order> findByIdAndStoreId(String id, String storeId);

    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);
}