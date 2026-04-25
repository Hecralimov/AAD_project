package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.dto.IndividualAnalyticsDTO;
import com.ecommerce.analytics_backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IndividualAnalyticsService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public IndividualAnalyticsDTO getIndividualDashboard(String userId) {
        BigDecimal totalSpent = orderRepository.calculateTotalSpentByUserId(userId);
        Long orderCount = orderRepository.countByUserId(userId);
        List<Map<String, Object>> statusBreakdown = orderRepository.getOrderStatusDistribution(userId);

        return IndividualAnalyticsDTO.builder()
                .totalSpent(totalSpent != null ? totalSpent : BigDecimal.ZERO)
                .orderCount(orderCount != null ? orderCount : 0L)
                .statusDistribution(statusBreakdown)
                .build();
    }
}