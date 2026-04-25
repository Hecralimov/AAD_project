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

@Service
@RequiredArgsConstructor
public class CorporateAnalyticsService {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public CorporateAnalyticsDTO getAnalyticsForOwner(String ownerId) {
        // 1. Find the store using the user's ID
        Store store = storeRepository.findByOwnerId(ownerId)
            .orElseThrow(() -> new RuntimeException("No store found for user ID: " + ownerId));

        String storeId = store.getId();

        // 2. Run aggregations scoped strictly to this store
        BigDecimal revenue = orderRepository.calculateTotalRevenueByStoreId(storeId);
        Long orders = orderRepository.countByStoreId(storeId);
        Long lowStock = productRepository.countLowStockProducts(storeId);

        // 3. Build and return DTO
        return CorporateAnalyticsDTO.builder()
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .orderCount(orders != null ? orders : 0L)
                .lowStockCount(lowStock != null ? lowStock : 0L)
                .build();
    }
}