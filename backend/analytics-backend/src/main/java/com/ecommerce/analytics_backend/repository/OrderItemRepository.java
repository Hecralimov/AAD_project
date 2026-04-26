package com.ecommerce.analytics_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.analytics_backend.dto.CategorySalesDTO;
import com.ecommerce.analytics_backend.model.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    @Query("SELECT new com.ecommerce.analytics_backend.dto.CategorySalesDTO(c.name, SUM(oi.unitPrice * oi.quantity)) " +
            "FROM OrderItem oi, Product p, Category c " +
            "WHERE oi.productId = p.id AND p.categoryId = c.id " +
            "GROUP BY c.name " +
            "ORDER BY SUM(oi.unitPrice * oi.quantity) DESC")
    List<CategorySalesDTO> getTopSellingCategories();
}
