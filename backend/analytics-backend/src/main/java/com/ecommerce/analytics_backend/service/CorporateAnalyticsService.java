package com.ecommerce.analytics_backend.service;

import com.ecommerce.analytics_backend.dto.CorporateAnalyticsDTO;
import com.ecommerce.analytics_backend.model.Store;
import com.ecommerce.analytics_backend.repository.OrderRepository;
import com.ecommerce.analytics_backend.repository.ProductRepository;
import com.ecommerce.analytics_backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class CorporateAnalyticsService {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CorporateAnalyticsDTO getAnalyticsForOwner(String ownerId, LocalDate startDate, LocalDate endDate) {
        Store store = getValidStore(ownerId);

        String storeId = store.getId();
        BigDecimal revenue;
        Long orders;

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX); // End of the day
            revenue = orderRepository.calculateTotalRevenueByStoreIdAndDateRange(storeId, start, end);
            orders = orderRepository.countByStoreIdAndDateRange(storeId, start, end);
        } else {
            // Fallback to lifetime stats if no dates are selected
            revenue = orderRepository.calculateTotalRevenueByStoreId(storeId);
            orders = orderRepository.countByStoreId(storeId);
        }

        Long lowStock = productRepository.countLowStockProducts(storeId);

        // Fixed the builder to match your DTO's totalOrders field
        return CorporateAnalyticsDTO.builder()
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .totalOrders(orders != null ? orders : 0L)
                .lowStockCount(lowStock != null ? lowStock : 0L)
                .build();
    }

    private Store getValidStore(String ownerId) {
        return storeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Store not found for owner with ID: " + ownerId));
    }
}
