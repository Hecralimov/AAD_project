package com.ecommerce.analytics_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.analytics_backend.model.OrderItem;
import com.ecommerce.analytics_backend.dto.DashboardAnalyticsDto;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    @Query("SELECT new com.ecommerce.analytics_backend.dto.DashboardAnalyticsDto$CategorySalesDto(c.name, COUNT(oi)) " +
           "FROM OrderItem oi " +
           "JOIN Product p ON oi.productId = p.id " +
           "JOIN Category c ON p.categoryId = c.id " +
           "GROUP BY c.name")
    List<DashboardAnalyticsDto.CategorySalesDto> getSalesByCategory();
}
