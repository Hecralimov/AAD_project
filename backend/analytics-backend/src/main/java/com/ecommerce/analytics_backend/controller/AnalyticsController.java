package com.ecommerce.analytics_backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.analytics_backend.dto.CategorySalesDTO;
import com.ecommerce.analytics_backend.dto.MonthlyRevenueProjection;
import com.ecommerce.analytics_backend.dto.RoleDistributionDTO;
import com.ecommerce.analytics_backend.dto.SentimentDistributionDTO;
import com.ecommerce.analytics_backend.repository.OrderItemRepository;
import com.ecommerce.analytics_backend.repository.OrderRepository;
import com.ecommerce.analytics_backend.repository.ReviewRepository;
import com.ecommerce.analytics_backend.repository.ShipmentRepository;
import com.ecommerce.analytics_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final ShipmentRepository shipmentRepository;

    @GetMapping("/users/distribution")
    public ResponseEntity<List<RoleDistributionDTO>> getUserDistribution() {
        return ResponseEntity.ok(userRepository.getRoleDistribution());
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        BigDecimal total = orderRepository.calculateTotalRevenue();
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }

    @GetMapping("/sales/categories")
    public ResponseEntity<List<CategorySalesDTO>> getCategorySales() {
        return ResponseEntity.ok(orderItemRepository.getTopSellingCategories());
    }

    @GetMapping("/reviews/sentiment")
    public ResponseEntity<List<SentimentDistributionDTO>> getReviewSentiments() {
        return ResponseEntity.ok(reviewRepository.getSentimentDistribution());
    }

    @GetMapping("/orders/total")
    public ResponseEntity<Long> getTotalOrders() {
        return ResponseEntity.ok(orderRepository.count());
    }

    @GetMapping("/shipments/pending")
    public ResponseEntity<Long> getPendingShipments() {
        // Assuming your database uses 'PENDING' as the status string
        return ResponseEntity.ok(shipmentRepository.countByStatus("PENDING"));
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<List<MonthlyRevenueProjection>> getMonthlyRevenue() {
        return ResponseEntity.ok(orderRepository.getMonthlyRevenue());
    }
}